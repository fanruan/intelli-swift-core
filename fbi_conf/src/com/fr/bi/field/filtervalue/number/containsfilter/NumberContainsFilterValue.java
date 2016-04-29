/**
 *
 */
package com.fr.bi.field.filtervalue.number.containsfilter;

import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

/**
 * @author Daniel
 *
 */
public class NumberContainsFilterValue extends NumberValuesFilterValue {

	/**
	 *
	 */
	private static final long serialVersionUID = -6973324281928100499L;

	@Override
	public boolean isMatchValue(Number value) {
		return valueSet.contains(value.doubleValue());
	}


    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader, long userId) {
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId);
        ICubeTableService ti = loader.getTableIndex(target);
        return gvi == null ? ti.getAllShowIndex()
                : gvi;
    }

}