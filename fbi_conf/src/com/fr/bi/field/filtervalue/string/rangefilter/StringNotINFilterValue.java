package com.fr.bi.field.filtervalue.string.rangefilter;

import com.fr.bi.field.filtervalue.string.StringFilterValueUtils;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;


public class StringNotINFilterValue extends StringRangeFilterValue {
    /**
     *
     */
    private static final long serialVersionUID = -4232644886359223498L;
    private static String XML_TAG = "StringNotINFilterValue";

    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId);
        ICubeTableService ti = loader.getTableIndex(target.getTableSource());
        return gvi == null ? GVIFactory.createAllEmptyIndexGVI()
                : gvi.NOT(loader.getTableIndex(target.getTableSource()).getRowCount()).AND(ti.getAllShowIndex());
    }

    @Override
    public boolean isMatchValue(String v) {
        return !valueSet.contains(v);
    }

    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        String value = StringFilterValueUtils.toString(node.getShowValue());
        if (valueSet.getValues() == null || valueSet.getValues().isEmpty()) {
            return false;
        }
        return isMatchValue(value);
    }
}