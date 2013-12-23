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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.widget.ImageView;

public class PassImageView extends ImageView {
	private int resourceID = 0;
	Paint paintRed;
	Rect rect = new Rect();

	public PassImageView(Context context) {
		super(context);
		paintRed = new Paint();
		paintRed.setColor(Color.RED);
		paintRed.setAlpha(150);
		paintRed.setStyle(Style.FILL_AND_STROKE);
		paintRed.setAntiAlias(true);
		paintRed.setStrokeWidth(3);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.getClipBounds(rect);
		canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paintRed);
		canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, paintRed);
	}

	public int getResourceID() {
		return resourceID;
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		this.resourceID = resId;
	}

}
