/*******************************************************************************
 * Copyright 2014 Marcel Walch, Florian Schaub
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
package de.uulm.graphicalpasswords.openuyi;

import java.util.Arrays;

import de.uulm.graphicalpasswords.R;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

public class UYILoginActivity extends Activity implements OnClickListener {

	private Vibrator vibrator;

	private int password_length;
	private String password;
	private int[] clickedPictures;

	private UYIPagerAdapter pageradapter;

	private int countClicks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uyi_login);
		// Show the Up button in the action bar.
		setupActionBar();

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		password_length = Integer.parseInt(sharedPref.getString("uyi_length",
				"10"));
		password = sharedPref.getString("uyi_pw", "");
		clickedPictures = new int[password_length];

		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		countClicks = 0;

		pageradapter = new UYIPagerAdapter(this, this);
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(pageradapter);
		viewPager.setCurrentItem(0);
	}

	@Override
	public void onClick(View v) {
		vibrator.vibrate(25);
		clickedPictures[countClicks] = v.getId();
		countClicks++;
		if (countClicks == password_length) {
			Arrays.sort(clickedPictures);
			DialogFragment dialog;
			if (password.equals(Arrays.toString(clickedPictures))) {
				dialog = new ResultOKDialog();
			} else {
				dialog = new WrongResultDialog();
			}
			dialog.show(getFragmentManager(), "result");
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
		getMenuInflater().inflate(R.menu.uyi_login, menu);
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

	public static class ResultOKDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.msg_pw_correct).setNeutralButton(
					R.string.btn_ok, new DialogInterface.OnClickListener() {

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
					R.string.btn_ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							getActivity().finish();
						}
					});
			return builder.create();
		}
	}

}
