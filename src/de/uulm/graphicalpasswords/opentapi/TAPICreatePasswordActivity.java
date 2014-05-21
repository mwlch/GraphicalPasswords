/*******************************************************************************
 * Copyright 2013 Marcel Walch, Florian Schaub
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.uulm.graphicalpasswords.opentapi;

import java.util.Arrays;
import java.util.Random;

import de.uulm.graphicalpasswords.opentapi.PassImageView;
import de.uulm.graphicalpasswords.R;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;
import android.widget.TableRow.LayoutParams;
import android.support.v4.app.NavUtils;
import android.text.Editable;

public class TAPICreatePasswordActivity extends Activity implements
		de.uulm.graphicalpasswords.opentapi.TAPI {

	private int[] images = new int[16];
	private PassImageView[] imageViews = new PassImageView[16];
	private Editable text;
	private Button back;
	private Button save;

	private String[] input;
	private int length;
	private int currentIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tapi_create_password);

		// Show the Up button in the action bar.
		setupActionBar();

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		length = Integer.parseInt(sharedPref.getString("tapi_length", "6"));
		input = new String[length];

		// Save Pictures in randomized order
		SharedPreferences.Editor editor = sharedPref.edit();
		Random random = new Random(System.currentTimeMillis());
		boolean[] usedImages = new boolean[TAPIActivity.IMAGES.length];
		Arrays.fill(usedImages, false);
		for (int i = 0; i < TAPIActivity.IMAGES.length; i++) {
			boolean pictureSet = false;
			int index = random.nextInt(TAPIActivity.IMAGES.length);
			do {
				if (!usedImages[index]) {
					editor.putInt("image" + i, TAPIActivity.IMAGES[index]);
					pictureSet = true;
					usedImages[index] = true;
				} else if (index == TAPIActivity.IMAGES.length - 1) {
					index = 0;
				} else {
					index++;
				}
			} while (!pictureSet);
		}
		editor.commit();

		for (int i = 0; i < images.length; i++) {
			images[i] = sharedPref.getInt("image" + i, 0);
		}

		EditText editText = (EditText) findViewById(R.id.tapi_edittext);
		text = editText.getText();
		back = (Button) findViewById(R.id.tapi_back);
		save = (Button) findViewById(R.id.tapi_save);

		TableLayout table = (TableLayout) findViewById(R.id.tapi_imagetable);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		TableRow row = null;
		for (int i = 0; i < imageViews.length; i++) {
			if (i % 4 == 0) {
				row = new TableRow(this);
			}
			imageViews[i] = new PassImageView(this);
			imageViews[i].setLayoutParams(layoutParams);
			imageViews[i].setAdjustViewBounds(true);
			imageViews[i].setScaleType(ScaleType.CENTER_INSIDE);
			imageViews[i].setPadding(5, 5, 5, 5);
			imageViews[i].setImageResource(images[i]);
			row.addView(imageViews[i]);
			if (i % 4 == 3) {
				table.addView(row);
			}
			imageViews[i].setOnTouchListener(new TAPIOnTouchListener(this,
					imageViews[i]));
		}

		Bundle bundle = new Bundle();
		bundle.putInt("length", length);
		DialogFragment intro = new IntroDialogFragment();
		intro.setArguments(bundle);
		intro.show(getFragmentManager(), "intro");
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
		getMenuInflater().inflate(R.menu.tapi_create_password, menu);
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

	@Override
	public void addInput(int resID, int field) {
		text.append("*");
		input[currentIndex] = "" + resID + ":" + field;
		currentIndex++;
		back.setClickable(true);
		back.setEnabled(true);
		if (currentIndex == input.length) {
			save.setClickable(true);
			save.setEnabled(true);
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[i].setClickable(false);
				imageViews[i].setEnabled(false);
			}
		}
	}

	public void unDo(View view) {
		currentIndex--;
		input[currentIndex] = "";
		text.delete(currentIndex, currentIndex + 1);
		if (currentIndex == 0) {
			back.setClickable(false);
			back.setEnabled(false);
		}
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setClickable(true);
			imageViews[i].setEnabled(true);
		}
		save.setClickable(false);
		save.setEnabled(false);
	}

	public void submit(View view) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit = sharedPref.edit();
		edit.putString("tapi_pw", arrayToString(input));
		edit.commit();

		Bundle bundle = new Bundle();
		bundle.putStringArray("input", input);
		DialogFragment dialog = new RememberPasswordDialogFragment();
		dialog.setArguments(bundle);
		dialog.show(getFragmentManager(), "remember");
	}

	protected static String arrayToString(String[] array) {
		String result = "";
		for (int i = 0; i < array.length; i++) {
			result += "[" + array[i] + "]";
		}
		return result;
	}

	public static class IntroDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			String message = getString(R.string.msg_dialog_tapi_createpw,
					getArguments().getInt("length"));
			builder.setTitle(R.string.title_dialog_tapi_createpw)
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
			LayoutInflater layoutInflater = (LayoutInflater) getActivity()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = layoutInflater.inflate(
					R.layout.tapi_rememberpw_dialog, (ViewGroup) getActivity()
							.findViewById(R.id.tapi_rememberpw_gallery));

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(layout)
					.setTitle(R.string.title_dialog_tapi_rememberpw)
					.setPositiveButton(R.string.btn_done,
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									getActivity().finish();
								}
							});

			Dialog dialog = builder.create();

			Gallery g = (Gallery) layout
					.findViewById(R.id.tapi_rememberpw_gallery);
			g.setAdapter(new ImageAdapter(getActivity(), getArguments()
					.getStringArray("input")));

			return dialog;
		}
	}
}
