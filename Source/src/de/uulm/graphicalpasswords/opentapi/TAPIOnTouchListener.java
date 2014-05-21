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

import android.content.Context;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TAPIOnTouchListener implements OnTouchListener {

	protected TAPI activity;
	private Vibrator vibrator;
	private PassImageView view;

	public TAPIOnTouchListener(TAPI activity, PassImageView view) {
		super();
		this.activity = activity;
		this.view = view;
		vibrator = (Vibrator) ((Context) activity)
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			vibrator.vibrate(25);

			float x = event.getX();
			float y = event.getY();

			int field = 0;
			if (y < f1(x)) {
				if (y < f2(x)) {
					field = 1;
				} else {
					field = 2;
				}
			} else {
				if (y > f2(x)) {
					field = 3;
				} else {
					field = 4;
				}
			}
			activity.addInput(this.view.getResourceID(), field);

			return true;
		}
		return false;
	}

	private float f1(float x) {
		return x;
	}

	private float f2(float x) {
		return view.getHeight() - x;
	}

}
