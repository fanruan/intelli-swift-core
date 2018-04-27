package com.fr.swift.result.node.xnode;

import com.fr.swift.result.GroupNode;

/**
 * Created by Lyon on 2018/4/9.
 */
public interface XGroupNode {

    /**
     * 交叉表行表头groupBy结果。
     * 包含指标值xValues[getTargetLength][topGroupByRowCount]
     * targetLength为指标的个数，topGroupByRowCount为表头维度groupBy结果的行数，包括汇总行
     *
     * @return
     */
    XLeftNode getCrossLeftNode();

    /**
     * 交叉表列表头groupBy结果。只有维度值的groupBy结果，不包含指标值
     * @return
     */
    GroupNode getTopGroupNode();
}
