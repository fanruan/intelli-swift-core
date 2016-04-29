/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.accumulate;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

/**
 * @author Daniel
 *
 */
public class AccumulateResultDealer implements ResultDealer {
	
	private BIKey key;
	private Traversal<BIDataValue> travel;
	
	AccumulateResultDealer(BIKey key, Traversal<BIDataValue> travel){
		this.key = key;
		this.travel = travel;
	}

	@Override
	public void dealWith(final ICubeTableService ti, GroupValueIndex gvi, final int startCol) {
		
		gvi.Traversal(new SingleRowTraversalAction() {
			private double v = 0;
			@Override
			public void actionPerformed(int row) {
				Double d =  (Double) ti.getRow(key, row);
				if(d != null){
					v += d.doubleValue();
				}
				travel.actionPerformed(new BIDataValue(row, startCol, v));
			}
		});
	}

}