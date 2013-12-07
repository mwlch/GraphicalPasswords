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
package de.uulm.graphicalpasswords.passgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class DialogPatternView extends View {

	Paint paint = new Paint();
	Rect rect = new Rect();

	private final int COUNT_CELLS = 4;

	private Path fixedPath = new Path();
	private Path tmpPath = new Path();
	private Path dotPath = new Path();
	private Path tmpDotPath = new Path();

	private float d;
	private int offset;

	private int originalWidth = 0;
	private int originalHeight = 0;
	private boolean resized = false;

	public DialogPatternView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DialogPatternView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DialogPatternView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// white background
		canvas.getClipBounds(rect);
		offset = rect.height() - rect.width();
		offset = offset / 2;

		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);
		canvas.drawRect(rect.left, rect.top + offset, rect.right, rect.bottom
				- offset, paint);

		// column width
		d = rect.width() * 1f / COUNT_CELLS;

		// areas
		paint.setColor(Color.LTGRAY);
		paint.setStyle(Style.FILL);

		for (int i = 0; i < COUNT_CELLS; i++) {
			if (i == 2 || i == 5) {
				canvas.drawRect(rect.left + 1 * d, rect.top + offset + i * d,
						rect.left + 3 * d, rect.top + offset + (i + 1) * d,
						paint);

			} else {
				canvas.drawRect(rect.left, rect.top + offset + i * d, rect.left
						+ 1 * d, rect.top + offset + (i + 1) * d, paint);
				canvas.drawRect(rect.left + 3 * d, rect.top + offset + i * d,
						rect.left + 4 * d, rect.top + offset + (i + 1) * d,
						paint);
			}
		}

		// Black grid
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);

		for (int i = 0; i < COUNT_CELLS + 1; i++) {
			canvas.drawLine(rect.left, rect.top + offset + i * d, rect.right,
					rect.top + offset + i * d, paint);
			canvas.drawLine(rect.left + i * d, rect.top + offset, rect.left + i
					* d, rect.bottom - offset, paint);
		}

		// Stars
		paint.setStyle(Style.FILL);
		int starsize = 4;
		canvas.drawRect(rect.left + 2 * d - starsize, rect.top + offset + 2 * d
				- starsize, rect.left + 2 * d + starsize, rect.top + offset + 2
				* d + starsize, paint);
		canvas.drawRect(rect.left + 6 * d - starsize, rect.top + offset + 2 * d
				- starsize, rect.left + 6 * d + starsize, rect.top + offset + 2
				* d + starsize, paint);
		canvas.drawRect(rect.left + 2 * d - starsize, rect.top + offset + 6 * d
				- starsize, rect.left + 2 * d + starsize, rect.top + offset + 6
				* d + starsize, paint);
		canvas.drawRect(rect.left + 6 * d - starsize, rect.top + offset + 6 * d
				- starsize, rect.left + 6 * d + starsize, rect.top + offset + 6
				* d + starsize, paint);
		canvas.drawRect(rect.left + 4 * d - starsize, rect.top + offset + 4 * d
				- starsize, rect.left + 4 * d + starsize, rect.top + offset + 4
				* d + starsize, paint);

		// Scale Path
		if (originalWidth > 0 && originalHeight > 0 && !resized) {
			float faktor = (COUNT_CELLS * d) / originalWidth;
			float topoffset = (originalHeight * faktor - rect.height()) / 2;

			Matrix matrix = new Matrix();
			matrix.setScale(faktor, faktor);
			matrix.postTranslate(0, topoffset * -1f);
			fixedPath.transform(matrix);
			dotPath.transform(matrix);

			resized = true;
		}

		// fixedPath
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeWidth(3);
		canvas.drawPath(fixedPath, paint);

		// tmpPath
		canvas.drawPath(tmpPath, paint);

		// DotPath
		paint.setStyle(Style.FILL);
		canvas.drawPath(dotPath, paint);

		// tmpDotPath
		canvas.drawPath(tmpDotPath, paint);

	}

	public void setFixedPath(Path fixedPath) {
		this.fixedPath = fixedPath;
	}

	public void setDotPath(Path dotPath) {
		this.dotPath = dotPath;
	}

	public void setOriginalDimensions(int width, int height) {
		originalWidth = width;
		originalHeight = height;
	}
}
