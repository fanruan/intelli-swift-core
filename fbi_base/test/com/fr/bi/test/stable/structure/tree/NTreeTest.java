/**
 * 
 */
package com.fr.bi.test.stable.structure.tree;

import java.util.Comparator;

import com.fr.bi.stable.structure.tree.NTree;

import edu.emory.mathcs.backport.java.util.Arrays;
import junit.framework.TestCase;

/**
 * @author Daniel
 *
 */
public class NTreeTest extends TestCase {
	
	private static Comparator<Integer> topC = new Comparator<Integer>(){

		@Override
		public int compare(Integer o1, Integer o2) {
			return o2.compareTo(o1);
		}
		
	};
	
	private static Comparator<Integer> bottomC = new Comparator<Integer>(){

		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
		
	};
	

	public void testNTree (){
		int len = 10000000;
		Integer[] values = new Integer[len];
		for(int i = 0 ;i < len; i++){
			values[i] = (int)(Math.random() * len);
		}
		treeTest(values.clone(), 10);
		treeTest(values.clone(), 100);
		treeTest(values.clone(), 1000);
		treeTest(values.clone(), 100000);
	}
	
	public void testCriticalityAndNull(){
		int N = 10;
		NTree<Integer> topTree = new NTree<Integer>(topC, N);
		assertEquals(topTree.getLineValue(), null);
		int len = 5;
		Integer[] values = new Integer[len];
		for(int i = 0 ;i < len; i++){
			values[i] = (int)(Math.random() * len);
		}
		treeTest(values, 10);
		treeTest(values, 5);
		treeTest(values, 4);
		treeTest(values, 1);
		try {
			treeTest(values, 0);
		} catch (Exception e){
			assertEquals(e.getClass().getName(), UnsupportedOperationException.class.getName());
		}
	}
	
	private void treeTest(Integer[] values, int N){
		NTree<Integer> topTree = new NTree<Integer>(topC, N);
		for(Integer i : values){
			topTree.add(i);
		}
		NTree<Integer> bottomTree = new NTree<Integer>(bottomC, N);
		for(Integer i : values){
			bottomTree.add(i);
		}
		Arrays.sort(values);
		assertEquals(values[Math.max(0, values.length - N)], topTree.getLineValue());
		assertEquals(values[Math.min(N - 1, values.length - 1)], bottomTree.getLineValue());
	}

}