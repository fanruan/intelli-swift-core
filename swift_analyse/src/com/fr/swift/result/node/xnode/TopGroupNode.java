package com.fr.swift.result.node.xnode;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.GroupNode;

import java.util.List;

/**
 * Created by Lyon on 2018/4/10.
 */
public class TopGroupNode extends GroupNode<TopGroupNode> {

    // topGroupValues.size()等于xLeftNode的总行数（包括普通行和汇总行）
    private List<AggregatorValue[]> topGroupValues;

    public TopGroupNode(int deep, Object data) {
        super(deep, data);
    }

    public List<AggregatorValue[]> getTopGroupValues() {
        return topGroupValues;
    }

    public void setTopGroupValues(List<AggregatorValue[]> topGroupValues) {
        this.topGroupValues = topGroupValues;
    }
}
