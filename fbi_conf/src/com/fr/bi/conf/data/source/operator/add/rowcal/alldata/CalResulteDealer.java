/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.alldata;

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
public class CalResulteDealer implements ResultDealer {
	
	private AllDataCalculator cal;
	private BIKey key;
	private Traversal<BIDataValue> travel;
	
	CalResulteDealer(BIKey key, AllDataCalculator cal, Traversal<BIDataValue> travel){
		this.key = key;
		this.cal = cal;
		this.travel = travel;
	}

	@Override
	public void dealWith(ICubeTableService ti, GroupValueIndex gvi, final int startCol) {
		final double v = cal.get(ti, key, gvi);
		gvi.Traversal(new SingleRowTraversalAction() {
			@Override
			public void actionPerformed(int row) {
				travel.actionPerformed(new BIDataValue(row, startCol, v));
			}
		});
	}

}