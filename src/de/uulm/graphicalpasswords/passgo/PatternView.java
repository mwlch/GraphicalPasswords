package de.uulm.graphicalpasswords.passgo;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PatternView extends View implements View.OnTouchListener {

	private Path fixedPath = new Path();
	private Path tmpPath = new Path();
	private Path dotPath = new Path();
	private Path tmpDotPath = new Path();

	private final int COUNT_CELLS = 4;

	private float d;
	private int offset;

	private float lastXc;
	private float lastYc;
	private boolean validInput = false;
	private boolean dot = false;

	private ArrayList<Coordinates> input = new ArrayList<Coordinates>();

	private PassGo activity;

	private int currentlength = 0;
	private int length;

	private Rect rect = new Rect();
	Paint paint = new Paint();

	public PatternView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnTouchListener(this);
	}

	public PatternView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
	}

	public PatternView(Context context) {
		super(context);
		this.setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// white canvas
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
			if (i == 0 || i == 3) {

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

		// black grid
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// input coordinates
		float x = event.getX();
		float y = event.getY();

		// nearest intersection
		int gridX = Math.round(x * 1f / d);
		int gridY = Math.round((y * 1f - offset) / d);

		// coordinates of intersection
		float gridXc = gridX * d;
		float gridYc = offset + gridY * d;

		// input outside of the grid
		if (y < offset - 3 / d || y > offset + 8 * d + 1) {
			tmpPath.reset();
			tmpDotPath.reset();
			return true;
		}

		// reaching password length
		if (currentlength == length) {
			return true;
		}

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (Math.sqrt(Math.pow(x - gridXc, 2) + Math.pow(y - gridYc, 2)) <= d / 3) {
				tmpDotPath.addCircle(gridXc, gridYc, 6, Direction.CW);
				dot = true;
				lastXc = gridXc;
				lastYc = gridYc;
				validInput = true;
				this.invalidate();
				input.add(new Coordinates(gridX, gridY));
			}
			break;
		case MotionEvent.ACTION_MOVE:
			tmpPath.reset();
			if (validInput) {
				if (Math.sqrt(Math.pow(x - gridXc, 2) + Math.pow(y - gridYc, 2)) <= d / 3) {
					fixedPath.moveTo(lastXc, lastYc);
					fixedPath.lineTo(gridXc, gridYc);
					lastXc = gridXc;
					lastYc = gridYc;
					tmpPath.reset();
					tmpDotPath.reset();
					Coordinates lastElement = input.get(input.size() - 1);
					if (!lastElement.equals(new Coordinates(gridX, gridY))) {
						input.add(new Coordinates(gridX, gridY));
						dot = false;
						currentlength++;
					}
				} else {
					tmpPath.moveTo(lastXc, lastYc);
					tmpPath.lineTo(x, y);
				}
			}
			invalidate();
			checkLength();
			break;
		case MotionEvent.ACTION_UP:
			if (validInput) {
				if (dot) {
					dotPath.addCircle(gridXc, gridYc, 6, Direction.CW);
					currentlength++;// rawData.add(new
									// RawData(event.getEventTime(),
									// "ACTION_UP Circle x:"+gridXc+"y:"+gridYc+" input x:"+x+" input y:"+y));
				} else if (Math.sqrt(Math.pow(x - gridXc, 2)
						+ Math.pow(y - gridYc, 2)) <= d / 3) {
					fixedPath.moveTo(lastXc, lastYc);
					fixedPath.lineTo(gridXc, gridYc);
				}
				input.add(new Coordinates(-1, -1));
			}
			tmpPath.reset();
			tmpDotPath.reset();
			validInput = false;
			invalidate();
			checkLength();
			break;
		}
		return true;
	}

	private void checkLength() {
		if (currentlength == length)
			activity.setReady(true);
		else
			activity.setReady(false);
	}

	public void clear() {
		fixedPath.reset();
		tmpPath.reset();
		dotPath.reset();
		tmpDotPath.reset();
		invalidate();
		input = new ArrayList<Coordinates>();
		currentlength = 0;
		activity.setReady(false);
	}

	public ArrayList<Coordinates> getInput() {
		return input;
	}

	public void setActivity(PassGo app) {
		activity = app;
	}

	public Path getFixedPath() {
		return fixedPath;
	}

	public Path getDotPath() {
		return dotPath;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
