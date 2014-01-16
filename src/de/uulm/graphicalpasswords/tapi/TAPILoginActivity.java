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
package de.uulm.graphicalpasswords.tapi;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;
import android.widget.TableRow.LayoutParams;
import android.support.v4.app.NavUtils;
import android.text.Editable;

public class TAPILoginActivity extends Activity implements TAPI {
	private int[] images = new int[16];
	private String[] input;
	private String password;
	private int length;
	private Editable text;

	private Button back;
	private Button clear;
	private Button save;

	private PassImageView[] imageViews = new PassImageView[16];

	private int currentIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tapi_login);
		// Show the Up button in the action bar.
		setupActionBar();

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		length = Integer.parseInt(sharedPref.getString("tapi_length", "0"));
		password = sharedPref.getString("tapi_pw", "");
		input = new String[length];

		for (int i = 0; i < images.length; i++) {
			images[i] = sharedPref.getInt("image" + i, 0);
		}

		EditText editText = (EditText) findViewById(R.id.tapi_edittext);
		text = editText.getText();
		back = (Button) findViewById(R.id.tapi_back);
		clear = (Button) findViewById(R.id.tapi_clear);
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
	}

	@Override
	public void addInput(int resID, int field) {
		text.append("*");
		input[currentIndex] = "" + resID + ":" + field;
		currentIndex++;
		back.setClickable(true);
		back.setEnabled(true);
		clear.setClickable(true);
		clear.setEnabled(true);
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
			clear.setClickable(false);
			clear.setEnabled(false);
		}
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setClickable(true);
			imageViews[i].setEnabled(true);
		}
		save.setClickable(false);
		save.setEnabled(false);
	}

	public void clearAll(View view) {
		for (int i = 0; i < currentIndex; i++) {
			input[i] = "";
		}
		currentIndex = 0;
		text.clear();
		back.setClickable(false);
		back.setEnabled(false);
		clear.setClickable(false);
		clear.setEnabled(false);
		save.setClickable(false);
		save.setEnabled(false);
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setClickable(true);
			imageViews[i].setEnabled(true);
		}
	}

	public void submit(View view) {
		DialogFragment dialog;
		if (password.equals(TAPICreatePasswordActivity.arrayToString(input))) {
			dialog = new ResultOKDialog();
		} else {
			dialog = new WrongResultDialog();
		}
		dialog.show(getFragmentManager(), "result");
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

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tapi_login, menu);
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

}
