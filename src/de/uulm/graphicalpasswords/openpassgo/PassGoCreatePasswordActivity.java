/*******************************************************************************
 * Copyright 2013 Marcel Walch, Florian Schaub
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
package de.uulm.graphicalpasswords.openpassgo;

import de.uulm.graphicalpasswords.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class PassGoCreatePasswordActivity extends Activity implements PassGo {

	private PatternView patternView;
	private Button save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pass_go_create_password);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		int length = Integer.parseInt(sharedPref
				.getString("passgo_length", "6"));

		patternView = (PatternView) findViewById(R.id.passgo_patternview);
		patternView.setActivity((PassGo) this);
		patternView.setLength(length);

		save = (Button) findViewById(R.id.passgo_save);
		setReady(false);

		Bundle bundle = new Bundle();
		bundle.putInt("length", length);
		DialogFragment intro = new IntroDialogFragment();
		intro.setArguments(bundle);
		intro.show(getFragmentManager(), "intro");

		// Show the Up button in the action bar.
		setupActionBar();
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

	public void clearAll(View view) {
		DialogFragment dialog = new DeletePatternDialogFragment();
		dialog.show(getFragmentManager(), "delete");
	}

	public void clearAll() {
		patternView.clear();
	}

	public void submit(View view) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit = sharedPref.edit();
		edit.putString("passgo_pw", patternView.getInput().toString());
		edit.commit();

		DialogFragment dialog = new RememberPasswordDialogFragment();
		dialog.show(getFragmentManager(), "remember");
	}

	public static class IntroDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			String message = getString(
					R.string.msg_dialog_pass_go_createpw,
					getArguments().getInt("length"));
			builder.setTitle(R.string.title_dialog_pass_go_createpw)
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

	public static class DeletePatternDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(
					R.string.msg_dialog_pass_go_deletepattern)
					.setCancelable(false)
					.setPositiveButton(R.string.btn_yes, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							((PassGoCreatePasswordActivity) getActivity())
									.clearAll();
						}
					})
					.setNegativeButton(R.string.btn_no, new OnClickListener() {

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
					R.layout.passgo_rememberpw_dialog,
					(ViewGroup) getActivity().findViewById(
							R.id.passgo_rememberpw_layout));

			PatternView patternView = ((PassGoCreatePasswordActivity) getActivity()).patternView;

			DialogPatternView patternViewDialog = (DialogPatternView) layout
					.findViewById(R.id.passgo_rememberpw_patternView);
			patternViewDialog.setFixedPath(patternView.getFixedPath());
			patternViewDialog.setDotPath(patternView.getDotPath());
			patternViewDialog.setOriginalDimensions(patternView.getWidth(),
					patternView.getHeight());

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(layout)
					.setTitle(R.string.title_dialog_pass_go_rememberpw)
					.setPositiveButton(R.string.btn_done,
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									getActivity().finish();
								}
							});
			return builder.create();
		}
	}

	public void setReady(boolean ready) {
		save.setClickable(ready);
		save.setEnabled(ready);
	}

}
