package de.uulm.graphicalpasswords.passgo;

import android.os.Parcel;
import android.os.Parcelable;

public class Coordinates implements Parcelable {
	private int x;
	private int y;

	/*
	 * This field is needed for Android to be able to create new objects,
	 * individually or as arrays.
	 */
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Coordinates createFromParcel(Parcel in) {
			return new Coordinates(in);
		}

		public Coordinates[] newArray(int size) {
			return new Coordinates[size];
		}
	};

	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coordinates(Parcel in) {
		this.x = in.readInt();
		this.y = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeInt(x);
		parcel.writeInt(y);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return "("+x+"|"+y+")";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass().equals(Coordinates.class)){
			return ((Coordinates) o).getX() == x && ((Coordinates) o).getY() == y;
		}
		return false;
	}
}
