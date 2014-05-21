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

public class ImageIndexTable {

	public final static int NEEDEDPICTURES = 512; // 2^9 (9 = INPUTLENGTH)
	public final static int PICTURELIMIT = 30;

	public static int m = 521; // mit 9 bit sind 512 bilder notwendig
	boolean[] isused;
	List<Integer> indexedpos = new ArrayList<Integer>();

	public ImageIndexTable() {
		this.isused = new boolean[m];
	}

	/**
	 * Returns an index of a image that wasn't used already. If we have more
	 * rounds than pictures than increase the offset, which means that all
	 * pictures are unused again
	 * 
	 * @param pos
	 * @return
	 */
	public int getIndex(int pos) {

		int offset = indexedpos.size() / PICTURELIMIT;

		pos = (pos % PICTURELIMIT) + (offset * PICTURELIMIT);

		if (pos > m - 1) {
			return 0;
		}

		if (isused[pos] == false) {

			isused[pos] = true;
			indexedpos.add(pos);
			return pos % PICTURELIMIT;
		} else
			return getIndex(pos, 1);
	}

	void initArray(boolean[] a, boolean init) {

		for (int i = 0; i < a.length; i++) {
			a[i] = false;
		}
	}

	protected int getIndex(final int pos, int i) {

		int offset = indexedpos.size() / PICTURELIMIT;

		int temp = ((pos + i) % PICTURELIMIT) + (offset * PICTURELIMIT);

		if (isused[temp] == false) {
			isused[temp] = true;
			indexedpos.add(temp);
			return temp % PICTURELIMIT;
		} else {
			i++;
			return getIndex(pos, i);
		}

	}

	public void removeLastIndex() {
		int end = 0;
		if (indexedpos.size() == 1)
			end = 1;
		else if (indexedpos.size() == 0)
			end = 0;
		else
			end = 2;

		for (int i = 0; i < end; i++) {
			int pos = indexedpos.get(indexedpos.size() - 1);
			isused[pos] = false;
			indexedpos.remove(indexedpos.size() - 1);
		}

	}

}
