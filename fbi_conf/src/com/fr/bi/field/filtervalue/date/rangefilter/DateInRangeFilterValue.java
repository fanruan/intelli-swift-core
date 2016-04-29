package com.fr.bi.field.filtervalue.date.rangefilter;

import com.fr.bi.stable.data.key.date.BIDay;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.LightNode;

public class DateInRangeFilterValue extends DateRangeFilterValue {


    /**
	 * 
	 */
	private static final long serialVersionUID = 5094504485033812162L;

	@Override
	public boolean dealWithNullValue() {
		return false;
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
            return false;
        }
        if (range.getEnd() != null && key.compareTo(range.getEnd()) > 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        return false;
    }
}