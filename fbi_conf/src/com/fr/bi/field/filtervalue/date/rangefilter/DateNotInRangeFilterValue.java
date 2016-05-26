/**
 *
 */
package com.fr.bi.field.filtervalue.date.rangefilter;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.data.key.date.BIDay;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;


public class DateNotInRangeFilterValue extends DateRangeFilterValue {


    /**
	 * 
	 */
	private static final long serialVersionUID = -7532680033686822207L;

	/**
     * 获取过滤后的索引
     *
     *
     * @param target
     * @param loader loader对象
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId).NOT(loader.getTableIndex(target.getTableSource()).getRowCount());

        return gvi.AND(loader.getTableIndex(target.getTableSource()).getAllShowIndex());
    }

	@Override
	public boolean dealWithNullValue() {
		return true;
	}
	
    /**
     * 属于范围内
     *
     * @param key 时间点
     * @return 是否在内
     */
    @Override
	protected boolean inRange(BIDay key) {
        if (range.getStart() != null && key.compareTo(range.getStart()) < 0) {
            return true;
        }
        if (range.getEnd() != null && key.compareTo(range.getEnd()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        return false;
    }
}