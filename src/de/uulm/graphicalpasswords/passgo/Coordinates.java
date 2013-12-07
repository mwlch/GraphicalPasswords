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
