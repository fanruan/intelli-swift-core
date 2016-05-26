package com.fr.bi.field.filtervalue.date.nonefilter;

import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;


public class DateNotNullFilterValue extends DateNullFilterValue {

    /**
     *
     */
    private static final long serialVersionUID = 4639231565765338327L;

    /**
     * 获取过滤后的索引
     *
     * @param target
     * @param loader loader对象
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId);
        return gvi != null ? gvi.NOT(loader.getTableIndex(target.getTableSource()).getRowCount()).AND(loader.getTableIndex(target.getTableSource()).getAllShowIndex())
                : loader.getTableIndex(target.getTableSource()).getAllShowIndex();
    }

    @Override
    public boolean isMatchValue(Long v) {
        return v != null;
    }

}