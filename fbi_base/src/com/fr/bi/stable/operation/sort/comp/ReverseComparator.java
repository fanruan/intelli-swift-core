/**
 * 
 */
package com.fr.bi.stable.operation.sort.comp;

import java.util.Comparator;

/**
 * @author Daniel
 *
 */
public class ReverseComparator<T> implements IComparator<T> {
	
	private Comparator<T> c;
	
	ReverseComparator(Comparator<T> c){
		this.c = c;
	}

	@Override
	public int compare(T o1, T o2) {
		return c.compare(o2, o1);
	}

}