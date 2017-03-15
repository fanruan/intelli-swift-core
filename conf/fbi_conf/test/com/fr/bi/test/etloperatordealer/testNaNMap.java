/**
 * 
 */
package com.fr.bi.test.etloperatordealer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fr.bi.base.FinalInt;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.operation.sort.comp.IComparator;

import edu.emory.mathcs.backport.java.util.Arrays;
import junit.framework.TestCase;

/**
 * @author Daniel
 *
 */
public class testNaNMap extends TestCase {
	
	public void testNaN(){
		Map<Double, Integer>  map = new HashMap<Double, Integer>(); 
		map.put(Double.NaN, 1);
		assertEquals(map.containsKey(Double.NaN), true);
		assertEquals(map.get(Double.NaN), new Integer(1));
		assertEquals(Double.NaN, Double.NaN);
	}
	
	public void testNaNSort(){
		int row = 100;
		Double[] a = new Double[row];
		for(int i = 0; i < row; i++){
			a[i] = Math.random() * row;
			if( i == row /2){
				a[i] = null;
			}
		}
		Arrays.sort(a, new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				if(o1 == o2){
					return 0;
				}
				if(o1 == null){
					return 1;
				}
				if(o2 == null){
					return -1;
				}
				return o2.compareTo(o1);
			}
		});
		System.out.println(a[0]);
		System.out.println(a[row - 1]);
	}
	
	public void testMap(){
		IComparator comparator =  ComparatorFacotry.DOUBLE_DESC;
		final TreeMap<Number, FinalInt> tree = new TreeMap<Number, FinalInt>(comparator);
		tree.put(122L, null);
		tree.put(122L, null);
	}

}