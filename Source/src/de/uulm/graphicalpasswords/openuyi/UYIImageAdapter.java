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

import java.util.LinkedList;
import java.util.Random;

import de.uulm.graphicalpasswords.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class UYIImageAdapter extends BaseAdapter {

	private Context context;
	private int galleryItemBackground;

	private LinkedList<Picture> pictureList = new LinkedList<Picture>();

	public UYIImageAdapter(Context c) {
		context = c;
		TypedArray attr = context
				.obtainStyledAttributes(R.styleable.UYIGallery);
		galleryItemBackground = attr.getResourceId(
				R.styleable.UYIGallery_android_galleryItemBackground, 0);
		attr.recycle();

		int length = UYIActivity.ORIGINAL_PICTURES.length;

		boolean[] pictureUsed = new boolean[length];
		for (int i = 0; i < length; i++) {
			pictureUsed[i] = false;
		}

		// Read pictures randomly
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(length);
			boolean pictureset = false;
			do {
				if (!pictureUsed[index]) {
					int original = UYIActivity.ORIGINAL_PICTURES[index];
					int distorted = UYIActivity.DISTORTED_PICTURES[index];
					pictureList.add(new Picture(original, distorted));
					pictureUsed[index] = true;
					pictureset = true;
				} else {
					index++;
					if (index >= length) {
						index = 0;
					}
				}
			} while (!pictureset);
		}
	}

	public String getPictureList() {
		Picture[] tmp = new Picture[pictureList.size()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = pictureList.get(i);
		}
		return Picture.distortedToString(tmp);
	}

	@Override
	public int getCount() {
		return pictureList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getImageResource(int position) {
		return pictureList.get(position).getOriginal();
	}

	public int getDistortedImageResource(int position) {
		return pictureList.get(position).getDistorted();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		imageView.setImageResource(pictureList.get(position).getOriginal());
		imageView.setLayoutParams(new Gallery.LayoutParams(240, 240));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setBackgroundResource(galleryItemBackground);

		return imageView;
	}

	public void removePicture(int position) {
		pictureList.remove(position);
		notifyDataSetChanged();
	}

	public Picture getPicture(int position) {
		return pictureList.get(position);
	}

	public void addPicture(Picture pic) {
		pictureList.add(pic);
		notifyDataSetChanged();
	}

}
