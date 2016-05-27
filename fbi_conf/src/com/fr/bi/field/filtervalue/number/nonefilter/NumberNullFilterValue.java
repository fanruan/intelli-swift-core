package com.fr.bi.field.filtervalue.number.nonefilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;


public class NumberNullFilterValue extends NumberNoneValueFilterValue {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6292297241616352753L;

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
        return targetValue == null;
    }

    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        if (dimension.getRelationList() == null) {
            return null;
        }
        return super.createFilterIndex(dimension, target, loader, userId);
    }
}