/*******************************************************************************
 * Copyright 2014 Marcel Walch, Florian Schaub
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.uulm.graphicalpasswords.openmiba;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.uulm.graphicalpasswords.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;

public class MIBACreatePasswordActivity extends Activity {

	private ArrayList<Integer> seenPictureIds = new ArrayList<Integer>();

	// amount of booleans of one entry
	// one for each rectangle and one for shift
	public final static int INPUTLENGTH = 9;

	private TableLayout tblLayout;
	private LinearLayout linlGrid[][];
	private Button btnDone;
	private Button btnBack;
	private TextView tvRound;

	private static String LOG_TAG = "MIBALoginActivity";
	private int width;
	private int height;
	private TouchListener touchlistener;

	int[][] colors_off = { { 0x3C000000, 0x3C000000, 0x3C000000, 0x3C000000 },
			{ 0x3C000000, 0x3C000000, 0x3C000000, 0x3C000000 } };

	public int min_rounds = 1;
	public int current_round = 1;
	public int rounds_to_do = 2;

	private boolean longpress = false;

	private ArrayList<Boolean> input = new ArrayList<Boolean>();
	private ImageIndexTable imgidxtable = new ImageIndexTable();

	private Handler handler = new MIBACreatePasswordHandler(
			new WeakReference<MIBACreatePasswordActivity>(this));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_miba_create_password);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		rounds_to_do = Integer.parseInt(sharedPref
				.getString("miba_length", "1"));
		min_rounds = rounds_to_do;

		Bundle bundle = new Bundle();
		bundle.putInt("length", rounds_to_do);
		DialogFragment intro = new IntroDialogFragment();
		intro.setArguments(bundle);
		intro.show(getFragmentManager(), "intro");

		// Show the Up button in the action bar.
		setupActionBar();

		setViews();
		seenPictureIds.add(R.drawable.ccp000);
	}

	public static class IntroDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			String message = getString(R.string.msg_dialog_miba_createpw,
					getArguments().getInt("length"));
			builder.setTitle(R.string.title_dialog_miba_createpw)
					.setMessage(message)
					.setNeutralButton(R.string.btn_ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			return builder.create();
		}
	}

	public static class RememberPasswordDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			MIBACreatePasswordActivity activity = (MIBACreatePasswordActivity) getActivity();

			LayoutInflater layoutInflater = (LayoutInflater) getActivity()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = layoutInflater.inflate(
					R.layout.miba_rememberpw_dialog, (Gallery) getActivity()
							.findViewById(R.id.miba_rememberpw_gallery));

			activity.unbindDrawables(activity.findViewById(R.id.tableLayout));
			System.gc();
			Gallery g = (Gallery) layout
					.findViewById(R.id.miba_rememberpw_gallery);
			g.setAdapter(new ImageAdapter(activity.getBaseContext(), activity
					.getSeenPictureIDs(), activity.getInput()));

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(layout)
					.setTitle(
							getResources().getString(
									R.string.title_dialog_miba_rememberpw))
					.setPositiveButton(
							getResources().getString(R.string.btn_done),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									SharedPreferences sharedPref = PreferenceManager
											.getDefaultSharedPreferences(getActivity());
									SharedPreferences.Editor edit = sharedPref
											.edit();
									edit.putString(
											"miba_pw",
											((MIBACreatePasswordActivity) getActivity())
													.buildPasswordString());
									edit.commit();
									dialog.dismiss();
									getActivity().finish();
								}
							});
			return builder.create();
		}

		private class ImageAdapter extends BaseAdapter {
			private int galleryItemBackground;
			private Context mContext;

			private ArrayList<Integer> pictureIDs;
			private ArrayList<Boolean> password;

			public ImageAdapter(Context c, ArrayList<Integer> seenPictureIds,
					ArrayList<Boolean> eingabe) {
				mContext = c;
				pictureIDs = seenPictureIds;
				this.password = eingabe;

				TypedArray attr = mContext
						.obtainStyledAttributes(R.styleable.UYIGallery);
				galleryItemBackground = attr
						.getResourceId(
								R.styleable.UYIGallery_android_galleryItemBackground,
								0);
				attr.recycle();
			}

			@Override
			public int getCount() {
				return pictureIDs.size() - 1;
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ImageView i = new ImageView(mContext);
				i.setScaleType(ImageView.ScaleType.FIT_XY);
				i.setBackgroundResource(galleryItemBackground);

				Bitmap b = BitmapFactory.decodeResource(getResources(),
						pictureIDs.get(position));
				Bitmap mutableBitmap = b.copy(Bitmap.Config.ARGB_8888, true);
				Canvas c = new Canvas(mutableBitmap);
				Paint p = new Paint();
				p.setColor(Color.RED);
				p.setStyle(Style.FILL);
				p.setAlpha(75);
				p.setAntiAlias(true);
				Paint p2 = new Paint();
				p2.setColor(Color.WHITE);
				p2.setStyle(Style.STROKE);
				p2.setStrokeWidth(5);
				p2.setAntiAlias(true);

				float imageWidth = mutableBitmap.getWidth() / 2;
				float imageHeight = mutableBitmap.getHeight() / 4;

				List<Boolean> tmp = new ArrayList<Boolean>();

				tmp = password.subList(position * INPUTLENGTH, position
						* INPUTLENGTH + 8);
				for (int j = 0; j < tmp.size(); j++) {
					switch (j) {
					case 0:
						if (tmp.get(j))
							c.drawRect(5, 5, imageWidth - 5, imageHeight - 5, p);
						c.drawRect(5, 5, imageWidth - 5, imageHeight - 5, p2);
						break;
					case 1:
						if (tmp.get(j))
							c.drawRect(imageWidth + 5, 5, 2 * imageWidth - 5,
									imageHeight - 5, p);
						c.drawRect(imageWidth + 5, 5, 2 * imageWidth - 5,
								imageHeight - 5, p2);
						break;
					case 2:
						if (tmp.get(j))
							c.drawRect(5, imageHeight + 5, imageWidth - 5,
									2 * imageHeight - 5, p);
						c.drawRect(5, imageHeight + 5, imageWidth - 5,
								2 * imageHeight - 5, p2);
						break;
					case 3:
						if (tmp.get(j))
							c.drawRect(imageWidth + 5, imageHeight + 5,
									2 * imageWidth - 5, 2 * imageHeight - 5, p);
						c.drawRect(imageWidth + 5, imageHeight + 5,
								2 * imageWidth - 5, 2 * imageHeight - 5, p2);
						break;
					case 4:
						if (tmp.get(j))
							c.drawRect(5, 2 * imageHeight + 5, imageWidth - 5,
									3 * imageHeight - 5, p);
						c.drawRect(5, 2 * imageHeight + 5, imageWidth - 5,
								3 * imageHeight - 5, p2);
						break;
					case 5:
						if (tmp.get(j))
							c.drawRect(imageWidth + 5, 2 * imageHeight + 5,
									2 * imageWidth - 5, 3 * imageHeight - 5, p);
						c.drawRect(imageWidth + 5, 2 * imageHeight + 5,
								2 * imageWidth - 5, 3 * imageHeight - 5, p2);
						break;
					case 6:
						if (tmp.get(j))
							c.drawRect(5, 3 * imageHeight + 5, imageWidth - 5,
									4 * imageHeight - 5, p);
						c.drawRect(5, 3 * imageHeight + 5, imageWidth - 5,
								4 * imageHeight - 5, p2);
						break;
					case 7:
						if (tmp.get(j))
							c.drawRect(imageWidth + 5, 3 * imageHeight + 5,
									2 * imageWidth - 5, 4 * imageHeight - 5, p);
						c.drawRect(imageWidth + 5, 3 * imageHeight + 5,
								2 * imageWidth - 5, 4 * imageHeight - 5, p2);
						break;
					}
				}

				i.setImageBitmap(mutableBitmap);

				return i;
			}

		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pass_go_create_password, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Transforms the user input to a passwordstring. This string may contain
	 * unprintable ASCII Codes.
	 * 
	 * @return
	 */
	public String buildPasswordString() {
		String res = "";

		for (int i = 0; i < input.size(); i++) {
			if (input.get(i)) {
				res += "1";
			} else {
				res += "0";
			}

		}
		return res;
	}

	public int boolToInt(boolean b) {
		if (b)
			return 1;
		return 0;
	}

	/**
	 * Returns the index of the next background image depending on the previous
	 * user input
	 * 
	 * @return
	 */
	public int getImageIndex() {
		if (input.size() < INPUTLENGTH)
			return -1;

		int res = 0;

		for (int i = 0; i < INPUTLENGTH; i++) {
			boolean a = input.get(input.size() - INPUTLENGTH + i); // least
																	// significant
																	// bit
																	// zuerst
			res = res << 1;
			res = res + boolToInt(a);
		}
		res = imgidxtable.getIndex(res);
		Log.i(LOG_TAG, "this entry lead to imageindex " + res);
		return res;
	}

	private void setViews() {
		tblLayout = (TableLayout) findViewById(R.id.tableLayout);
		btnBack = (Button) findViewById(R.id.miba_btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				back();
			}
		});
		btnDone = (Button) findViewById(R.id.miba_btnCreateMasterKey);

		tvRound = (TextView) this.findViewById(R.id.tvRound);
		tvRound.setText(getString(R.string.label_round)+" 1");

		linlGrid = new LinearLayout[2][4];
		linlGrid[0][0] = (LinearLayout) this.findViewById(R.id.square1);
		linlGrid[1][0] = (LinearLayout) this.findViewById(R.id.square2);
		linlGrid[0][1] = (LinearLayout) this.findViewById(R.id.square3);
		linlGrid[1][1] = (LinearLayout) this.findViewById(R.id.square4);
		linlGrid[0][2] = (LinearLayout) this.findViewById(R.id.square5);
		linlGrid[1][2] = (LinearLayout) this.findViewById(R.id.square6);
		linlGrid[0][3] = (LinearLayout) this.findViewById(R.id.square7);
		linlGrid[1][3] = (LinearLayout) this.findViewById(R.id.square8);

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 2; x++) {
				linlGrid[x][y].setBackgroundColor(colors_off[x][y]);
			}

		}

		// get width and height from mainpanel
		// can not use display width/height because of notification bar
		ViewTreeObserver vto = tblLayout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				// get values of portrait mode

				Display display = getWindowManager().getDefaultDisplay();

				if (display.getRotation() == Surface.ROTATION_0) {
					width = tblLayout.getWidth();
					height = tblLayout.getHeight();
				} else {
					height = tblLayout.getWidth();
					width = tblLayout.getHeight();
				}

				Bitmap bmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.ccp000);
				bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
				Drawable d = new BitmapDrawable(getResources(), bmp);
				tblLayout.setBackgroundDrawable(d);

				touchlistener = new TouchListener(linlGrid, width, height,
						handler);
				tblLayout.setOnTouchListener(touchlistener);
				// remove listener again otherwise it gets called twice
				tblLayout.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
			}
		});

		btnDone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DialogFragment dialog = new RememberPasswordDialogFragment();
				dialog.show(getFragmentManager(), "remember");
			}
		});
		btnDone.setVisibility(View.VISIBLE);
	}

	private void back() {

		setFinishable(false);

		// last state
		boolean isshift = touchlistener.getLastState();

		// not two long presses in sequence
		// remove last state and return the state of the last but one state
		boolean lastbutoneisshift = touchlistener.reset();

		// adapt round count
		if (!isshift) { // normal click
			if (current_round > 1)
				current_round--;
			if (lastbutoneisshift) { // back to shift screen
				tvRound.setText(getString(R.string.label_round) + " "
						+ current_round + " -Shift-");
			} else { // decrease round
				tvRound.setText(getString(R.string.label_round) + " "
						+ current_round);
			}

		} else { // shift -> do not decrease round, show current round
			tvRound.setText(getString(R.string.label_round) + " "
					+ current_round);
		}

		if (input.size() >= INPUTLENGTH)
			// input: 8 rectangles + 1 shift enabled flag
			for (int i = 0; i < INPUTLENGTH; i++) {
				input.remove(input.size() - 1);
			}

		// enable last index
		// if user inserts the same gesture -> show same picture again
		imgidxtable.removeLastIndex();

		// remove seen Pictures ...
		seenPictureIds.remove(seenPictureIds.size() - 1);
		seenPictureIds.remove(seenPictureIds.size() - 1);

		// ... and show previous background image
		int idx = getImageIndex();
		changeBackground(idx);
	}

	public void changeBackground(int imageindex) {
		int img = 0;
		if (imageindex == -1) {
			img = R.drawable.ccp000;
		} else
			img = R.drawable.ccp001 + imageindex;

		seenPictureIds.add(img);

		Bitmap bmp = BitmapFactory.decodeResource(getResources(), img);
		bmp = Bitmap.createScaledBitmap(bmp, width, height, true);

		Drawable d = new BitmapDrawable(getResources(), bmp);
		bmp = null; // prevent outofmemory
		tblLayout.setBackgroundDrawable(d);

	}

	public ArrayList<Gesture> getPasswordPictures() {
		ArrayList<Gesture> res = new ArrayList<Gesture>();
		int length = input.size() / INPUTLENGTH;
		for (int i = 0; i < length; i++) {
			boolean[] list = new boolean[INPUTLENGTH];
			for (int j = 0; j < list.length; j++) {
				list[j] = input.get(i * INPUTLENGTH + j);
			}
			res.add(new Gesture(seenPictureIds.get(i), list));
		}
		return res;
	}

	private void checkIfFinished() {
		if (current_round == rounds_to_do + 1) {
			setFinishable(true);
		}
	}

	private void setFinishable(boolean finish) {
		btnDone.setEnabled(finish);
		btnDone.setClickable(finish);
		if (finish)
			tblLayout.setOnTouchListener(null);
		else
			tblLayout.setOnTouchListener(touchlistener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindDrawables(findViewById(R.id.tableLayout));
		System.gc();
	}

	protected void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	protected ArrayList<Integer> getSeenPictureIDs() {
		return seenPictureIds;
	}

	protected ArrayList<Boolean> getInput() {
		return input;
	}

	private static class MIBACreatePasswordHandler extends Handler {
		MIBACreatePasswordActivity activity;

		private MIBACreatePasswordHandler(WeakReference<MIBACreatePasswordActivity> instance) {
			activity = instance.get();
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case TouchListener.MSG_ROUND_FINISHED: {
				activity.longpress = false;

				boolean[][] active = (boolean[][]) msg.obj;
				for (int y1 = 0; y1 < 4; y1++) {
					for (int x1 = 0; x1 < 2; x1++) {
						if (active[x1][y1] == true) {
							Log.i(LOG_TAG, "Rec x" + x1 + " y" + y1
									+ " is activated");
						}
						activity.input.add(active[x1][y1]);
					}
				}
				activity.input.add(false); // not shifted

				activity.current_round++;
				activity.tvRound.setText(activity
						.getString(R.string.label_round)
						+ " "
						+ activity.current_round);

				// show next image
				int idx = activity.getImageIndex();
				activity.changeBackground(idx);
				break;
			}
			case TouchListener.MSG_SHIFT_ROUND_FINISHED: {
				Vibrator v = (Vibrator) activity
						.getSystemService(Context.VIBRATOR_SERVICE);

				// 1. Vibrate for 300 milliseconds
				long milliseconds = 300;
				v.vibrate(milliseconds);

				activity.tvRound.setText(activity
						.getString(R.string.label_round)
						+ " "
						+ activity.current_round + " -Shift-");
				activity.longpress = true;

				boolean[][] shifted = (boolean[][]) msg.obj;

				for (int y1 = 0; y1 < 4; y1++) {
					for (int x1 = 0; x1 < 2; x1++) {
						if (shifted[x1][y1] == true) {
							Log.i(LOG_TAG, "Rec x" + x1 + " y" + y1
									+ " was shifted");

						}
						activity.input.add(shifted[x1][y1]);
					}
				}
				activity.input.add(true); // shifted true

				// show next image
				int idx = activity.getImageIndex();
				activity.changeBackground(idx);

				break;
			}
			}
			activity.checkIfFinished();
		}

	}
}
