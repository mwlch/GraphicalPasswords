package de.uulm.graphicalpasswords.openmiba;

import android.os.Parcel;
import android.os.Parcelable;

public class Gesture implements Parcelable {
	private int picID;
	private boolean[] gesture = new boolean[MIBACreatePasswordActivity.INPUTLENGTH];

	/*
	 * This field is needed for Android to be able to create new objects,
	 * individually or as arrays.
	 */
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Gesture createFromParcel(Parcel in) {
			return new Gesture(in);
		}

		public Gesture[] newArray(int size) {
			return new Gesture[size];
		}
	};

	public Gesture(int picID, boolean[] gesture) {
		this.picID = picID;
		this.gesture = gesture;
	}

	public Gesture(Parcel in) {
		this.picID = in.readInt();
		in.readBooleanArray(gesture);
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeInt(picID);
		parcel.writeBooleanArray(gesture);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public int getPicID() {
		return picID;
	}

	public boolean[] getGesture() {
		return gesture;
	}

	@Override
	public String toString() {
		String res = "(" + picID + ":";
		for (int i = 0; i < gesture.length; i++) {
			if (gesture[i]) {
				res += 1;
			} else {
				res += 0;
			}
		}
		res += ")";
		return res;
	}
}
