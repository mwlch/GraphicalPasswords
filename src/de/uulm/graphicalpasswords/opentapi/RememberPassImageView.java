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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class RememberPassImageView extends ImageView {
	private int field = 0;

	public RememberPassImageView(Context context) {
		super(context);

	}

	public void setHighlitedField(int n) {
		field = n;
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		Drawable drawable = getDrawable();
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		Rect rect = canvas.getClipBounds();
		paint.setAlpha(50);
		paint.setStyle(Style.FILL);
		Path path = new Path();
		switch (field) {
		case 1:
			path.moveTo(rect.left, rect.top);
			path.lineTo(rect.exactCenterX(), rect.exactCenterY());
			path.lineTo(rect.right, rect.top);
			break;
		case 2:
			path.moveTo(rect.right, rect.top);
			path.lineTo(rect.exactCenterX(), rect.exactCenterY());
			path.lineTo(rect.right, rect.bottom);
			break;
		case 3:
			path.moveTo(rect.right, rect.bottom);
			path.lineTo(rect.exactCenterX(), rect.exactCenterY());
			path.lineTo(rect.left, rect.bottom);
			break;
		case 4:
			path.moveTo(rect.left, rect.bottom);
			path.lineTo(rect.exactCenterX(), rect.exactCenterY());
			path.lineTo(rect.left, rect.top);
			break;
		default:
			break;
		}
		path.close();
		canvas.drawPath(path, paint);

		paint.setAlpha(150);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3);

		canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);
		canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, paint);

		setImageBitmap(bitmap);
	}

}
