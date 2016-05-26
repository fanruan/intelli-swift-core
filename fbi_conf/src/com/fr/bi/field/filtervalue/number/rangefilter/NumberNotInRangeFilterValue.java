package com.fr.bi.field.filtervalue.number.rangefilter;

import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

public class NumberNotInRangeFilterValue extends NumberRangeFilterValue {
    /**
     *
     */
    private static final long serialVersionUID = -3892255869004607182L;
    private static String XML_TAG = "NumberNotInRangeFilterValue";

    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId);
        return gvi.NOT(loader.getTableIndex(target.getTableSource()).getRowCount()).AND(loader.getTableIndex(target.getTableSource()).getAllShowIndex());
    }

    /**
     * 是否符合条件
     *
     * @param value 值
     * @return true或false
     */
    @Override
    public boolean matchValue(double v) {
        return (closemin ? v < min : v <= min) || (closemax ? v > max : v >= max);
    }

    @Override
    public boolean dealWithNullValue() {
        return true;
    }
}