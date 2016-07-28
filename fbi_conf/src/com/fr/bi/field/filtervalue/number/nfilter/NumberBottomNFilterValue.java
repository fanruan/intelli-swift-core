package com.fr.bi.field.filtervalue.number.nfilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.conf.report.widget.field.filtervalue.NFilterValue;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.LightNode;


public class NumberBottomNFilterValue extends NumberNFilterValue implements NFilterValue{
    /**
	 *
	 */
	private static final long serialVersionUID = -623836848692910399L;
	public static String XML_TAG = "NumberBottomNFilterValue";

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
        int comparedRow = parentNode.getChildLength() + 1 - n;
        if (comparedRow <= 0) {
            return true;
        }
        double nline = parentNode.getChildTOPNValueLine(targetKey, comparedRow);

        Number targetValue = node.getSummaryValue(targetKey);
        return targetValue == null ? false : targetValue.doubleValue() <= nline;
    }
}