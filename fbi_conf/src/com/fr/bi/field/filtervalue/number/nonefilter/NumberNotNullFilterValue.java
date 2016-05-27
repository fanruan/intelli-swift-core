package com.fr.bi.field.filtervalue.number.nonefilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;


public class NumberNotNullFilterValue extends NumberNoneValueFilterValue {

    /**
     *
     */
    private static final long serialVersionUID = -8602956598527061834L;

    /**
     * 是否显示记录
     *
     * @param node      节点
     * @param targetKey 指标信息
     * @return 是否显示
     */
    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        Number targetValue = node.getSummaryValue(targetKey);
        return targetValue != null;
    }

    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {
        if (dimension.getRelationList() == null) {
            return null;
        }
        return super.createFilterIndex(dimension, target, loader, userId).NOT(loader.getTableIndex(target).getRowCount())
                .AND(loader.getTableIndex(target).getAllShowIndex());
    }

    /**
     * 是否符合条件
     *
     * @param value 值
     * @return true或false
     */
    @Override
    public boolean isMatchValue(Number value) {
        return !super.isMatchValue(value);
    }
}