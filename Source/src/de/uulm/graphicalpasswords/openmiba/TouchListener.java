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
package de.uulm.graphicalpasswords.openmiba;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

public class TouchListener implements OnTouchListener {

	public static final int MSG_ROUND_FINISHED = 1;
	public static final int MSG_SHIFT_ROUND_FINISHED = 2;
	public static final int MSG_START_ROUND = 3;

	private static Handler handler;
	private boolean secclick = false;

	boolean isArrayEqual(boolean[][] a, boolean[][] b) {
		boolean equal = true;
		for (int y1 = 0; y1 < 4; y1++) {
			for (int x1 = 0; x1 < 2; x1++) {
				if (a[x1][y1] != b[x1][y1]) {
					equal = false;
				}
			}
		}
		return equal;
	}

	void initArray(boolean[][] a, boolean init) {

		for (int y1 = 0; y1 < 4; y1++) {
			for (int x1 = 0; x1 < 2; x1++) {
				a[x1][y1] = init;
			}
		}

	}

	void initArray(int[][] a, int init) {

		for (int y1 = 0; y1 < 4; y1++) {
			for (int x1 = 0; x1 < 2; x1++) {
				a[x1][y1] = init;
			}
		}

	}

	boolean[][] copyArray(boolean[][] a) {

		boolean[][] res = new boolean[2][4];

		for (int y1 = 0; y1 < 4; y1++) {
			for (int x1 = 0; x1 < 2; x1++) {
				res[x1][y1] = a[x1][y1];
			}
		}
		return res;

	}

	private Thread shiftchecker;

	class ShiftCheckerThread extends Thread {

		private boolean[][] laststate = new boolean[2][4];

		@Override
		public void run() {

			while (!cancel_shift) {
				Log.i(LOG_TAG, "i m thread checker " + getName());
				if (isArrayEqual(laststate, active_recs) && !cancel_shift) {
					Log.i(LOG_TAG, "thread: arrays are equal " + getName());
					Message msg = Message.obtain();
					msg.what = MSG_SHIFT_ROUND_FINISHED;
					msg.obj = copyArray(active_recs);
					handler.sendMessage(msg);
					longpress = true;
					return;

				} else {
					if (cancel_shift)
						return;

					laststate = copyArray(active_recs);

					try {
						Thread.sleep(750);
					} catch (InterruptedException e) {
						Log.i(LOG_TAG, "i got interupted from sleep "
								+ getName());
					}

				}

			}
			Log.i(LOG_TAG, "thread stopped " + getName());

		}

	}

	private static String LOG_TAG = "TouchListener";
	private final LinearLayout[][] linlGrid;
	private final int width;
	private final int height;

	// active rectangles
	boolean[][] active_recs = { { false, false, false, false },
			{ false, false, false, false } };

	boolean[][] up_recs = { { false, false, false, false },
			{ false, false, false, false } };
	boolean[][] secdown_recs = { { false, false, false, false },
			{ false, false, false, false } };

	int[][] activatedbypointerid = { { -1, -1, -1, -1 }, { -1, -1, -1, -1 } };

	private List<Boolean> isshifted = new ArrayList<Boolean>();

	private boolean longpress = false;
	private boolean cancel_shift = false;

	public TouchListener(LinearLayout[][] linlGrid, int width, int heigth,
			Handler hndl) {
		this.linlGrid = linlGrid;
		this.width = width;
		this.height = heigth;
		handler = hndl;
	}

	private int[] getRectangle(float x, float y) {
		int[] coords = new int[2];
		coords[0] = (int) x / (width / 2);
		coords[1] = (int) y / (height / 4);
		return coords;
	}

	void printSamples(MotionEvent ev) {
		final int historySize = ev.getHistorySize();
		final int pointerCount = ev.getPointerCount();
		for (int h = 0; h < historySize; h++) {
			System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
			for (int p = 0; p < pointerCount; p++) {
				System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p),
						ev.getHistoricalX(p, h), ev.getHistoricalY(p, h));
			}
		}
		System.out.printf("At time %d:", ev.getEventTime());
		for (int p = 0; p < pointerCount; p++) {
			System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p),
					ev.getX(p), ev.getY(p));
		}
	}

	/**
	 * If user hits the back button in LoginActivity, make sure longpress is
	 * available again or not. It's not allowed that two shifts are coming
	 * directly in a row long long short is not allowed
	 * 
	 * @return true if second last was shifted
	 */
	public boolean reset() {
		// erst ein element in der liste
		if (isshifted.size() <= 1) {
			secclick = false;
			isshifted.clear();
			return false;
		}

		boolean vorletzter = isshifted.get(isshifted.size() - 2);
		boolean letzter = isshifted.get(isshifted.size() - 1);

		// vorletzter klick war ein shift
		if (vorletzter) {
			secclick = true; // kein weiteren shift erlauben
		}
		// wenn der letzte klick ein shift war
		if (letzter) {
			secclick = false; // erlaube wieder ein shift
		}

		// letzten l�schen
		isshifted.remove(isshifted.size() - 1);
		return vorletzter;
	}

	public boolean getLastState() {
		if (isshifted.size() == 0)
			return false;
		boolean shift = isshifted.get(isshifted.size() - 1);
		for (int i = 0; i < isshifted.size(); i++) {
			Log.d(LOG_TAG, "" + isshifted.get(i));
		}
		if (shift) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// if (event.getAction() == MotionEvent.ACTION_MOVE) return true;
		int action = event.getAction() & MotionEvent.ACTION_MASK;

		int ptrIdx = event.getActionIndex();
		int ptrId = event.getPointerId(ptrIdx);

		if (action == MotionEvent.ACTION_DOWN) { // primary pointer has gone
													// down
			// Log.i(LOG_TAG, "First Down: PtrId: "+ ptrId + " PtrIdx: " +
			// ptrIdx);

			setRectangleStateMoving(event.getX(0), event.getY(0), true, 0,
					false);

			longpress = false;
			cancel_shift = false;
			Log.i(LOG_TAG, "frist pointer down: shift enabled");

			Message msg = Message.obtain();
			msg.what = MSG_START_ROUND;
			handler.sendMessage(msg);

			// start thread only after first click
			if (!secclick) {

				// thread is no longer alive. it got interrupted by last finger
				// up and thus terminated
				if (shiftchecker != null && shiftchecker.isAlive()) {

				} else {
					// thus always is created a new checker thread
					shiftchecker = new ShiftCheckerThread();
					shiftchecker.start();
				}

			} else {

			}

			// doubleClick(event, 0);

		} else if (action == MotionEvent.ACTION_UP) { // primary pointer has
														// gone up

			// Log.i(LOG_TAG, "Last UP PtrId: "+ ptrId + " PtrIdx: " + ptrIdx +
			// " " + event.getPointerCount());// describeEvent(event));

			setRectangleStateMoving(event.getX(0), event.getY(0), false, ptrId,
					false);

			// normal click
			if (!longpress || secclick) {
				Log.i(LOG_TAG, "last pointer up: shift canceled");
				cancel_shift = true;

				// wake up thread from sleeping => now it reaches the cancel
				// condition
				shiftchecker.interrupt();

				Message msg = Message.obtain();
				msg.what = MSG_ROUND_FINISHED;
				msg.obj = copyArray(active_recs);
				handler.sendMessage(msg);
				secclick = false;
				isshifted.add(false);
			}
			// long click
			else if (longpress) {
				secclick = true; // do not allow a subsequent long click
				isshifted.add(true);
			}

			initArray(active_recs, false);

		} else if (action == MotionEvent.ACTION_MOVE) { // none primary pointer
														// goes down

			/*
			 * ptrIdx = (event.getAction() &
			 * MotionEvent.ACTION_POINTER_INDEX_MASK) >>
			 * MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			 */
			// ptrId = event.getPointerId(ptrIdx);
			// int ptrIdx = event.getActionIndex();
			// int ptrId = event.getAction() >>
			// MotionEvent.ACTION_POINTER_ID_SHIFT;
			/*
			 * int ptrId = (event.getAction() &
			 * MotionEvent.ACTION_POINTER_INDEX_MASK) >>>
			 * MotionEvent.ACTION_POINTER_ID_SHIFT;
			 */

			final int pointerCount = event.getPointerCount();
			for (ptrIdx = 0; ptrIdx < pointerCount; ptrIdx++) {
				ptrId = event.getPointerId(ptrIdx);
				// Log.i(LOG_TAG, "Move PtrId: "+ ptrId + " PtrIdx: " + ptrIdx);

				setRectangleStateMoving(event.getX(ptrIdx), event.getY(ptrIdx),
						true, ptrId, true);

			}
		} else if (action == MotionEvent.ACTION_POINTER_DOWN) { // none primary
																// pointer goes
																// down

			// Log.i(LOG_TAG, "Further Down PtrId: "+ ptrId + " PtrIdx: " +
			// ptrIdx);// describeEvent(event));
			setRectangleStateMoving(event.getX(ptrIdx), event.getY(ptrIdx),
					true, ptrId, false);

			// doubleClick(event, ptrIdx);
		} else if (action == MotionEvent.ACTION_POINTER_UP) { // none primary
																// pointer goes
																// down
			// Log.i(LOG_TAG, "Further UP PtrId: "+ ptrId + " PtrIdx: " +
			// ptrIdx);// describeEvent(event));
			// switchOffOldActivation(ptrId);
			setRectangleStateMoving(event.getX(ptrIdx), event.getY(ptrIdx),
					false, ptrId, false);
		}

		return true;
	}

	private void setRectangleStateMoving(float x, float y, boolean active,
			int ptrID, boolean moving) {
		Log.d("coords", "" + x + " " + y);
		int[] coords = getRectangle(x, y);
		// Log.i(LOG_TAG, coords[0] + " " + coords[1]);

		Log.d("coords", "" + coords[0] + " " + coords[1]);
		// pointer within same rectangle => do nothing
		if (activatedbypointerid[coords[0]][coords[1]] == ptrID && moving) {
			return;
		}

		// deactivate or it is a moving event that points at a new rect
		// => deactivate old one
		if ((activatedbypointerid[coords[0]][coords[1]] != ptrID && moving)
				|| active == false) {
			// Log.i(LOG_TAG, "Moved to new Rectangle");
			// search old field
			for (int y1 = 0; y1 < 4; y1++) {
				for (int x1 = 0; x1 < 2; x1++) {
					if (activatedbypointerid[x1][y1] == ptrID) { 
						// deactivate old rectangle
						linlGrid[x1][y1].setVisibility(View.VISIBLE);
						if (moving) {
							activatedbypointerid[x1][y1] = -1;
							active_recs[x1][y1] = false;
						}

					}
				}
			}
		}

		if (active) {
			/*
			 * //make it darker float[] hsv = new float[3];
			 * Color.colorToHSV(color, hsv); hsv[2] *= 0.8f; color =
			 * Color.WHITE; //Color.HSVToColor(hsv);
			 */
			// Log.v(LOG_TAG, "Rectangle x" + coords[0] + " y" + coords[1]);

			active_recs[coords[0]][coords[1]] = true;
			activatedbypointerid[coords[0]][coords[1]] = ptrID;
			linlGrid[coords[0]][coords[1]].setVisibility(View.INVISIBLE);

		}
	}

	public String describeEvent(MotionEvent event) {
		StringBuilder result = new StringBuilder(500);
		result.append("Action: ").append(event.getAction()).append("\n");
		int numPointers = event.getPointerCount();
		result.append("Number of pointer: ").append(numPointers).append("\n");
		int ptrIdx = 0;
		while (ptrIdx < numPointers) {
			int ptrId = event.getPointerId(ptrIdx);
			result.append("Pointer Index: ").append(ptrIdx);
			result.append(", Pointer Id: ").append(ptrId).append("\n");
			result.append(" Location: ").append("x").append(event.getX(ptrIdx));
			result.append("y").append(event.getY(ptrIdx)).append("\n");
			ptrIdx++;
		}
		result.append("Downtime: ").append(event.getDownTime()).append("ms\n");
		result.append("Event time: ").append(event.getEventTime()).append("ms");
		result.append(" Elapsed: ").append(
				event.getEventTime() - event.getDownTime());
		result.append("ms\n");
		Log.i(LOG_TAG, result.toString());
		return result.toString();
	}

}
