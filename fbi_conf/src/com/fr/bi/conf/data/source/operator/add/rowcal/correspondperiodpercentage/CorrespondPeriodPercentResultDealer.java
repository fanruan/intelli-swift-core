/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiodpercentage;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel
 *
 */
public class CorrespondPeriodPercentResultDealer implements ResultDealer {
	
	private BIKey key;
	private BIKey periodKey;
	private Traversal<BIDataValue> travel;
	private static final Double PERCENT_DEFAULT = 0d;
	
	CorrespondPeriodPercentResultDealer(BIKey key, Traversal<BIDataValue> travel,  BIKey periodKey){
		this.key = key;
		this.travel = travel;
		this.periodKey = periodKey;
	}

	@Override
	public void dealWith(final ICubeTableService ti, GroupValueIndex gvi, final int startCol) {
		final Map<Double, Double> map = new HashMap<Double, Double>();
        final ICubeColumnDetailGetter getter = ti.getColumnDetailReader(periodKey);
        final ICubeColumnDetailGetter keyGetter = ti.getColumnDetailReader(CorrespondPeriodPercentResultDealer.this.key);
        gvi.Traversal(new SingleRowTraversalAction() {
			@Override
			public void actionPerformed(int row) {
				Number v = (Number)getter.getValue(row);
				if(v == null){
					return;
				}
				double key = v.doubleValue();
				if(!map.containsKey(key)){
					Number value = (Number)keyGetter.getValue(row);
					if(value == null){
						value = PERCENT_DEFAULT;
					}
					map.put(key, value.doubleValue());
				}
			}
		});
		gvi.Traversal(new SingleRowTraversalAction() {
			@Override
			public void actionPerformed(int row) {
				Number v = (Number) getter.getValue(row);
				Number value = null;
				if(v != null){
					double key = v.doubleValue() - 1;
					value = map.get(key);
				}
				Number currentValue = (Number)keyGetter.getValue(row);
				if(value == null){
					value = PERCENT_DEFAULT;
				}
				if(currentValue == null){
					currentValue = PERCENT_DEFAULT;
				}
				Double result = null;
				if(value.doubleValue() == currentValue.doubleValue()){
					result = PERCENT_DEFAULT;
				} else {
					result = (currentValue.doubleValue() - value.doubleValue())/value.doubleValue();
				}
				travel.actionPerformed(new BIDataValue(row, startCol, result));
			}
		});
	}

}