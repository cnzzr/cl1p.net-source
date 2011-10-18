package com.diodesoftware.util;

import java.util.Comparator;

public class PairCompare implements Comparator {

	public int compare(Object o1, Object o2) {
		Pair p1 = (Pair)o1;
		Pair p2 = (Pair)o2;
		if(p1.v > p2.v)
			return -1;
		else if(p1.v < p2.v)
			return 1;
		return 0;
	}

}
