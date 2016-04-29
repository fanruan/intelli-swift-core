/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.line;

import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.structure.tree.NTree;

/**
 * @author Daniel
 *
 */
public abstract class AbstractNLine implements CalLineGetter {
	
	protected int N;
	/**
	 * @param gvi
	 * @param ti
	 * @param key
	 */
	AbstractNLine(int N) {
		this.N = N;
	}

	
	protected abstract NTree<Double> getNTree();
	
	@Override
	public Double getCalLine(final ICubeTableService ti, final BIKey key, GroupValueIndex gvi) {
		if(gvi == null){
			return null;
		}
		final NTree<Double> tree = getNTree(); 
		gvi.Traversal(new SingleRowTraversalAction() {
			@Override
			public void actionPerformed(int data) {
				Double v = ((Number) ti.getRow(key, data)).doubleValue();
				if(v != null){
					tree.add(v);
				}
			}
		});
		return tree.getLineValue();
	}

}