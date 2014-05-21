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

import de.uulm.graphicalpasswords.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class MIBALoginActivity extends Activity {

	private String password;

	// amount of booleans of one entry
	// one for each rectangle and one for shift
	public final static int INPUTLENGTH = 9;

	private TableLayout tableLayout;
	// private LinearLayout linlBottom;
	private LinearLayout linlGrid[][];
	private Button btnBack;
	private TextView tvRound;

	private static String LOG_TAG = "LoginActivity";
	private int width;
	private int height;
	private TouchListener touchlistener;

	int[][] colors_off = { { 0x3C000000, 0x3C000000, 0x3C000000, 0x3C000000 },
			{ 0x3C000000, 0x3C000000, 0x3C000000, 0x3C000000 } };

	boolean firsttime = false;
	public int min_rounds = 2;
	public int current_round = 1;
	public int rounds_to_do = 2;

	private boolean longpress = false;

	private ArrayList<Boolean> input = new ArrayList<Boolean>();
	private ImageIndexTable imgidxtable = new ImageIndexTable();

	private Handler handler = new MIBALoginHandler(
			new WeakReference<MIBALoginActivity>(this));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_miba_login);

		// Show the Up button in the action bar.
		setupActionBar();

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		rounds_to_do = Integer.parseInt(sharedPref
				.getString("miba_length", "1"));
		min_rounds = rounds_to_do;
		password = sharedPref.getString("miba_pw", "");

		setViews();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pass_go_login, menu);
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

	private void setViews() {
		tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		btnBack = (Button) findViewById(R.id.miba_btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				back();
			}
		});

		tvRound = (TextView) this.findViewById(R.id.tvRound);
		tvRound.setText(getString(R.string.label_round) + " 1");

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
		ViewTreeObserver vto = tableLayout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				// get values of portrait mode

				Display display = getWindowManager().getDefaultDisplay();

				if (display.getRotation() == Surface.ROTATION_0) {
					width = tableLayout.getWidth();
					height = tableLayout.getHeight();
				} else {
					height = tableLayout.getWidth();
					width = tableLayout.getHeight();
				}

				Bitmap bmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.ccp000);
				bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
				Drawable d = new BitmapDrawable(getResources(), bmp);
				bmp = null; // prevent outofmemor
				tableLayout.setBackgroundDrawable(d);

				touchlistener = new TouchListener(linlGrid, width, height,
						handler);
				tableLayout.setOnTouchListener(touchlistener);
				// remove listener again otherwise it gets called twice
				tableLayout.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
			}
		});
	}

	private void back() {
		// last state
		boolean isshift = touchlistener.getLastState();

		// not two long presses in sequence
		// remove last state and return the state of the last but one state
		boolean vorletztzerisshift = touchlistener.reset();

		// adapt round count
		if (!isshift) { // normal click
			if (current_round > 1)
				current_round--;
			if (vorletztzerisshift) { // back to shift screen
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

		// ... and show previous background image
		int idx = getImageIndex();
		changeBackground(idx);
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
																	// first
			res = res << 1;
			res = res + boolToInt(a);
		}
		res = imgidxtable.getIndex(res);
		Log.i(LOG_TAG, "this entry lead to imageindex " + res);
		return res;
	}

	public void changeBackground(int imageindex) {
		int img = 0;
		if (imageindex == -1) {
			img = R.drawable.ccp000;
		} else
			img = R.drawable.ccp001 + imageindex;

		Bitmap bmp = BitmapFactory.decodeResource(getResources(), img);
		bmp = Bitmap.createScaledBitmap(bmp, width, height, true);

		Drawable d = new BitmapDrawable(getResources(), bmp);
		bmp = null; // prevent outofmemory
		tableLayout.setBackgroundDrawable(d);

	}

	public int boolToInt(boolean b) {
		if (b)
			return 1;
		return 0;
	}

	public void checkIfFinished() {
		if (current_round == min_rounds + 1) {
			tableLayout.setOnClickListener(null);

			DialogFragment dialog;
			if (buildPasswordString().equals(password)) {
				dialog = new ResultOKDialog();
			} else {
				dialog = new WrongResultDialog();
			}
			dialog.show(getFragmentManager(), "result");
		}
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

	public static class ResultOKDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.msg_pw_correct).setNeutralButton(
					R.string.btn_ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							getActivity().finish();
						}
					});
			return builder.create();
		}
	}

	public static class WrongResultDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.msg_pw_wrong).setNeutralButton(
					R.string.btn_ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							getActivity().finish();
						}
					});
			return builder.create();
		}
	}

	private static class MIBALoginHandler extends Handler {
		MIBALoginActivity activity;

		private MIBALoginHandler(WeakReference<MIBALoginActivity> instance) {
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

				activity.checkIfFinished();
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
				activity.checkIfFinished();
				break;
			}
			}

		}
	}
}
