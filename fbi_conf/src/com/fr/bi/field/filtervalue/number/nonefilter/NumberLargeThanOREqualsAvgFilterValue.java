package com.fr.bi.field.filtervalue.number.nonefilter;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.report.widget.field.filtervalue.NFilterValue;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;


public class NumberLargeThanOREqualsAvgFilterValue extends NumberNoneValueFilterValue implements NFilterValue {


    /**
	 * 
	 */
	private static final long serialVersionUID = 5310442954148672836L;

	/**
     * 是否显示记录
     *
     * @param node      节点
     * @param targetKey 指标信息
     * @return 是否显示
     */
    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        LightNode parentNode = node.getParent();
        double nline = parentNode.getChildAVGValue(targetKey);
        //FIXME 不存在的值怎么处理呢
        Number targetValue = node.getSummaryValue(targetKey);
        return targetValue == null ? false : targetValue.doubleValue() >= nline;
    }

    /**
     * 获取过滤后的索引
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        return null;
    }
}