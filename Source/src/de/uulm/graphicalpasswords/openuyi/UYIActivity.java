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

import de.uulm.graphicalpasswords.R;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class UYIActivity extends Activity {

	protected static final int[] ORIGINAL_PICTURES = { R.drawable.o1,
			R.drawable.o2, R.drawable.o3, R.drawable.o4, R.drawable.o5,
			R.drawable.o6, R.drawable.o7, R.drawable.o8, R.drawable.o9,
			R.drawable.o10, R.drawable.o11, R.drawable.o12, R.drawable.o13,
			R.drawable.o14, R.drawable.o15, R.drawable.o16, R.drawable.o17,
			R.drawable.o18, R.drawable.o19, R.drawable.o20, R.drawable.o21,
			R.drawable.o22, R.drawable.o23, R.drawable.o24, R.drawable.o25,
			R.drawable.o26, R.drawable.o27 };
	protected static final int[] DISTORTED_PICTURES = { R.drawable.w1,
			R.drawable.w2, R.drawable.w3, R.drawable.w4, R.drawable.w5,
			R.drawable.w6, R.drawable.w7, R.drawable.w8, R.drawable.w9,
			R.drawable.w10, R.drawable.w11, R.drawable.w12, R.drawable.w13,
			R.drawable.w14, R.drawable.w15, R.drawable.w16, R.drawable.w17,
			R.drawable.w18, R.drawable.w19, R.drawable.w20, R.drawable.w21,
			R.drawable.w22, R.drawable.w23, R.drawable.w24, R.drawable.w25,
			R.drawable.w26, R.drawable.w27 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uyi);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Button login = (Button) findViewById(R.id.btn_login);
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sharedPref.getString("uyi_pw", "").equals(""))
			login.setEnabled(false);
		else
			login.setEnabled(true);
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
		getMenuInflater().inflate(R.menu.uyi, menu);
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
		case R.id.action_settings:
			Intent intent = new Intent(this,
					de.uulm.graphicalpasswords.openuyi.UYISettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public void startCreatePassword(View view) {
		Intent intent = new Intent(this,
				de.uulm.graphicalpasswords.openuyi.UYICreatePasswordActivity.class);
		startActivity(intent);
	}

	public void startEnterPassword(View view) {
		Intent intent = new Intent(this,
				de.uulm.graphicalpasswords.openuyi.UYILoginActivity.class);
		startActivity(intent);
	}

}
