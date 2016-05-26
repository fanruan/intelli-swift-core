package com.fr.bi.field.filtervalue.number.evenfilter;

import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;

import java.math.BigDecimal;


public class NumberNotEqualFilterValue extends NumberEvenFilterValue {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3179573765187490573L;

	/**
     * 显示节点
     *
     * @param node      节点
     * @param targetKey 指标信息
     * @return true或false
     */
    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        Number targetValue = node.getSummaryValue(targetKey);
        if (targetValue == null) {
            return false;
        }
        double v = targetValue.doubleValue();
        if (Double.isNaN(v)) {
            return false;
        }
        BigDecimal v1 = new BigDecimal(v);
        BigDecimal v2 = new BigDecimal(V);
        return v1.compareTo(v2) != 0;
    }

    /**
     * 创建索引
     * @return 索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        return super.createFilterIndex(dimension, target, loader, userId);
    }

    /**
     * 是否匹配
     * @param value 值
     * @return true或false
     */
    @Override
    public boolean isMatchValue(Number value) {
        return !super.isMatchValue(value);
    }

    @Override
    public boolean canCreateFilterIndex() {
        return false;
    }


}