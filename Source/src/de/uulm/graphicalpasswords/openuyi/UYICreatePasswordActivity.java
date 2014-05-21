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
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.TableRow.LayoutParams;
import android.support.v4.app.NavUtils;

public class UYICreatePasswordActivity extends Activity {

	private Vibrator vibrator;

	private int length;

	private ImageView[] originalViews;
	private ImageView[] distortedViews;

	private Picture[] selectedPictures;

	private Gallery gallery;
	private TableLayout table;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uyi_create_password);
		// Show the Up button in the action bar.
		setupActionBar();

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		length = Integer.parseInt(sharedPref.getString("uyi_length", "10"));

		originalViews = new ImageView[length];
		distortedViews = new ImageView[length];
		selectedPictures = new Picture[length];

		Bundle bundle = new Bundle();
		bundle.putInt("length", length);
		DialogFragment intro = new IntroDialogFragment();
		intro.setArguments(bundle);
		intro.show(getFragmentManager(), "intro");

		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		Arrays.fill(selectedPictures, null);

		gallery = (Gallery) findViewById(R.id.uyi_gallery_originals);
		gallery.setAdapter(new UYIImageAdapter(this));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				ImageView viewOriginal = new ImageView(
						UYICreatePasswordActivity.this);
				ImageView viewDistorted = new ImageView(
						UYICreatePasswordActivity.this);

				int i = 0;
				for (; i < selectedPictures.length; i++) {
					if (i == selectedPictures.length - 1
							&& selectedPictures[i] != null) {
						removePicture(i);
					}
					if (selectedPictures[i] == null
							|| i == selectedPictures.length - 1) {
						viewOriginal = originalViews[i];
						viewDistorted = distortedViews[i];
						selectedPictures[i] = ((UYIImageAdapter) parent
								.getAdapter()).getPicture(position);
						vibrator.vibrate(100);

						ScrollView sv = (ScrollView) findViewById(R.id.uyi_choosepi_scrollview);
						int height = originalViews[0].getMeasuredHeight();
						sv.scrollTo(0, (i * height) - 200);

						break;
					}
				}

				int originalImageResource = ((UYIImageAdapter) parent
						.getAdapter()).getImageResource(position);
				viewOriginal.setImageResource(originalImageResource);
				viewDistorted.setImageResource(((UYIImageAdapter) parent
						.getAdapter()).getDistortedImageResource(position));
				((UYIImageAdapter) parent.getAdapter()).removePicture(position);

				OnLongClickListener listener = new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						vibrator.vibrate(200);
						int viewid = v.getId();
						int index = -1;

						for (int i = 0; i < originalViews.length; i++) {
							if (originalViews[i].getId() == viewid) {
								index = i;
								break;
							} else if (distortedViews[i].getId() == viewid) {
								index = i;
								break;
							}
						}
						Bundle bundle = new Bundle();
						bundle.putInt("index", index);
						DialogFragment dialog = new DeleteImageDialogFragment();
						dialog.setArguments(bundle);
						dialog.show(getFragmentManager(), "delete");
						return false;
					}
				};

				viewOriginal.setOnLongClickListener(listener);
				viewDistorted.setOnLongClickListener(listener);

				// Check
				int count = 0;
				for (int j = 0; j < selectedPictures.length; j++) {
					if (selectedPictures[j] != null) {
						count++;
					}
				}
				if (count == selectedPictures.length) {
					findViewById(R.id.uyi_save).setClickable(true);
					findViewById(R.id.uyi_save).setEnabled(true);
				}
			}
		});

		table = (TableLayout) findViewById(R.id.uyi_choosepi_tablelayout);
		LayoutParams params = new LayoutParams(
				android.widget.TableRow.LayoutParams.WRAP_CONTENT,
				android.widget.TableRow.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		for (int i = 0; i < length; i++) {
			TableRow row = new TableRow(table.getContext());

			originalViews[i] = new ImageView(row.getContext());
			originalViews[i].setAdjustViewBounds(true);
			originalViews[i].setScaleType(ScaleType.FIT_XY);
			originalViews[i].setPadding(3, 3, 3, 3);
			originalViews[i].setImageResource(R.drawable.oempty);
			originalViews[i].setId(100 + i);

			distortedViews[i] = new ImageView(row.getContext());
			distortedViews[i].setAdjustViewBounds(true);
			distortedViews[i].setScaleType(ScaleType.FIT_XY);
			distortedViews[i].setPadding(3, 3, 3, 3);
			distortedViews[i].setImageResource(R.drawable.wempty);
			distortedViews[i].setId(1000 + i);

			ImageView arrow = new ImageView(row.getContext());
			arrow.setAdjustViewBounds(true);
			arrow.setScaleType(ScaleType.FIT_XY);
			arrow.setMaxWidth(80);
			arrow.setPadding(3, 3, 3, 3);
			arrow.setImageResource(R.drawable.arrow_active);
			arrow.setLayoutParams(params);

			row.addView(originalViews[i]);
			row.addView(arrow);
			row.addView(distortedViews[i]);
			table.addView(row);
		}
	}

	private void removePicture(int index) {
		findViewById(R.id.uyi_save).setClickable(false);
		findViewById(R.id.uyi_save).setEnabled(false);

		ImageView orig = originalViews[index];
		ImageView dist = distortedViews[index];
		orig.setOnLongClickListener(null);
		dist.setOnLongClickListener(null);
		orig.setImageResource(R.drawable.oempty);
		dist.setImageResource(R.drawable.wempty);
		orig.setClickable(false);
		dist.setClickable(false);
		((UYIImageAdapter) gallery.getAdapter())
				.addPicture(selectedPictures[index]);
		selectedPictures[index] = null;
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
		getMenuInflater().inflate(R.menu.uyi_create_password, menu);
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

	public void submit(View view) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit = sharedPref.edit();
		edit.putString("uyi_pw",
				Picture.distortedToSortedString(selectedPictures));
		edit.commit();

		Bundle bundle = new Bundle();
		bundle.putIntArray("input",
				Picture.distortedToIntArray(selectedPictures));
		DialogFragment dialog = new RememberPasswordDialogFragment();
		dialog.setArguments(bundle);
		dialog.show(getFragmentManager(), "remember");
	}

	public static class IntroDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			String message = getString(R.string.msg_dialog_uyi_createpw,
					getArguments().getInt("length"));
			builder.setTitle(R.string.title_dialog_uyi_createpw)
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

	public static class DeleteImageDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			DeleteOnClickListener deleteListener = new DeleteOnClickListener(
					getArguments().getInt("index", -1));
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.msg_dialog_uyi_deleteimage)
					.setCancelable(false)
					.setPositiveButton(R.string.btn_yes, deleteListener)
					.setNegativeButton(R.string.btn_no, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			return builder.create();
		}

		class DeleteOnClickListener implements DialogInterface.OnClickListener {

			private int deletePicture;

			public DeleteOnClickListener(int deletePicture) {
				super();
				this.deletePicture = deletePicture;
			}

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (which == Dialog.BUTTON_POSITIVE && deletePicture != -1) {
					((UYICreatePasswordActivity) getActivity())
							.removePicture(deletePicture);
				}
			}
		}
	}

	public static class RememberPasswordDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			LayoutInflater layoutInflater = (LayoutInflater) getActivity()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = layoutInflater.inflate(
					R.layout.uyi_rememberpw_dialog, (ViewGroup) getActivity()
							.findViewById(R.id.uyi_rememberpw_gallery));

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(layout)
					.setTitle(R.string.title_dialog_uyi_rememberpw)
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
					.findViewById(R.id.uyi_rememberpw_gallery);
			g.setAdapter(new ImageAdapter(getActivity(), getArguments()
					.getIntArray("input")));

			return dialog;
		}
	}
}
