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

import java.util.Random;

import de.uulm.graphicalpasswords.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class UYIPagerAdapter extends PagerAdapter {

	private int[] pictures_page1 = new int[9];
	private int[] pictures_page2 = new int[9];
	private int[] pictures_page3 = new int[9];

	private int[][] picture_arrays = { pictures_page1, pictures_page2,
			pictures_page3 };

	private OnClickListener clickListener;

	public UYIPagerAdapter(OnClickListener listener, Activity activity) {
		super();
		this.clickListener = listener;

		int length = UYIActivity.DISTORTED_PICTURES.length;

		boolean[] pictureUsed = new boolean[length];
		for (int i = 0; i < length; i++) {
			pictureUsed[i] = false;
		}

		int[] pictures = UYIActivity.DISTORTED_PICTURES;

		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 3; j++) {
				int index = random.nextInt(length);
				boolean pictureset = false;
				do {
					if (!pictureUsed[index]) {
						picture_arrays[j][i] = pictures[index];
						pictureUsed[index] = true;
						pictureset = true;
					} else {
						index++;
						if (index >= pictures.length) {
							index = 0;
						}
					}
				} while (!pictureset);
			}
		}
	}

	@Override
	public void destroyItem(View collection, int arg1, Object object) {
		((ViewPager) collection).removeView((View) object);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Object instantiateItem(View collection, int pos) {
		LayoutInflater inflater = (LayoutInflater) collection.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int page = 0;

		switch (pos) {
		case 1:
			page = R.layout.uyipage2;
			break;
		case 2:
			page = R.layout.uyipage3;
			break;
		default:
			page = R.layout.uyipage1;
			break;
		}

		View view = inflater.inflate(page, null);
		TableLayout currentTable;

		switch (pos) {
		case 1:
			currentTable = (TableLayout) view
					.findViewById(R.id.uyi_tablelayout_page2);
			break;
		case 2:
			currentTable = (TableLayout) view
					.findViewById(R.id.uyi_tablelayout_page3);
			break;
		default:
			currentTable = (TableLayout) view
					.findViewById(R.id.uyi_tablelayout_page1);
			break;
		}

		// Add ImageButtons to TableLayout
		int index = 0;
		for (int i = 0; i < 3; i++) {
			TableRow currentRow = new TableRow(view.getContext());
			for (int j = 0; j < 3; j++) {
				ImageButton button = new ImageButton(view.getContext());
				button.setBackgroundColor(Color.TRANSPARENT);
				button.setPadding(3, 3, 3, 3);
				button.setAdjustViewBounds(true);
				button.setOnClickListener(clickListener);
				button.setImageResource(picture_arrays[pos][index]);
				button.setId(picture_arrays[pos][index]);
				button.setClickable(true);
				index++;
				currentRow.addView(button);
			}
			currentTable.addView(currentRow);
		}

		((ViewPager) collection).addView(view, 0);

		return view;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub
	}

	public String getPictureSet() {
		String set1 = "";
		String set2 = "";
		String set3 = "";
		for (int i = 0; i < pictures_page1.length; i++) {
			set1 += "[" + pictures_page1[i] + "]";
			set2 += "[" + pictures_page2[i] + "]";
			set3 += "[" + pictures_page3[i] + "]";
		}
		return "(" + set1 + ")(" + set2 + ")(" + set3 + ")";
	}
}
