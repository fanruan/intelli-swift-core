package com.fr.bi.field.filtervalue.string.rangefilter;

import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.Set;


public class StringINFilterValue extends StringRangeFilterValue {
    /**
     *
     */
    private static final long serialVersionUID = 3125282687396992999L;
    private static String XML_TAG = "StringINFilterValue";

    public StringINFilterValue() {
    }

    public StringINFilterValue(Set<String> valueSet) {
        this.valueSet = new StringValueSet(valueSet, StringValueSet.CONTAINS);
    }



    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader, long userId) {
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId);
        ICubeTableService ti = loader.getTableIndex(target);
        return gvi == null ? getIndexWhenNull(ti)
                : gvi;
    }

    private GroupValueIndex getIndexWhenNull(ICubeTableService ti) {
        return ti.getAllShowIndex();
    }

    
    @Override
    public boolean isMatchValue(String v) {
    	return valueSet.contains(v);
    }
}