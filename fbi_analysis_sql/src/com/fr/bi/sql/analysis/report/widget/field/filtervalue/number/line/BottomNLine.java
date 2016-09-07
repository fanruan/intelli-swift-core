/**
 * 
 */
package com.fr.bi.sql.analysis.report.widget.field.filtervalue.number.line;

import com.fr.bi.stable.structure.tree.NTree;

import java.util.Comparator;

/**
 * @author Daniel
 *
 */
public class BottomNLine extends AbstractNLine {
	
	/**
	 * @param gvi
	 * @param ti
	 * @param key
	 * @param N
	 */
	public BottomNLine(int N) {
		super(N);
	}

	private static final Comparator<Double> BOTTOMC = new Comparator<Double>() {

		@Override
		public int compare(Double o1, Double o2) {
			return o1.compareTo(o2);
		}
	};

	@Override
	protected NTree<Double> getNTree() {
		return  new NTree<Double>(BOTTOMC, N);
	}

}