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
package de.uulm.graphicalpasswords.uyi;

import java.util.Arrays;

public class Picture {

	private int original;
	private int distorted;

	public Picture(int original, int distorted) {
		this.original = original;
		this.distorted = distorted;
	}

	public int getOriginal() {
		return original;
	}

	public void setOriginal(int original) {
		this.original = original;
	}

	public int getDistorted() {
		return distorted;
	}

	public void setDistorted(int distorted) {
		this.distorted = distorted;
	}

	public static String distortedToSortedString(Picture[] pics) {
		int[] arr = distortedToIntArray(pics);
		Arrays.sort(arr);
		return Arrays.toString(arr);
	}

	public static String distortedToString(Picture[] pics) {
		return Arrays.toString(distortedToIntArray(pics));
	}

	public static int[] distortedToIntArray(Picture[] pics) {
		int[] arr = new int[pics.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = pics[i].getDistorted();
		}
		return arr;
	}
}
