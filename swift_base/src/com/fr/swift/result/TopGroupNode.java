package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.List;

/**
 * Created by Lyon on 2018/4/10.
 */
public class TopGroupNode extends GroupNode<TopGroupNode> {

    // topGroupValues.size()等于xLeftNode的总行数（包括普通行和汇总行）
    private List<AggregatorValue[]> topGroupValues;
    // 临时保存列索引
    private RowTraversal traversal;

    public TopGroupNode(int deep, Object data) {
        super(deep, data);
    }

    public TopGroupNode(int deep, int segmentIndex) {
        super(deep, segmentIndex);
    }

    public void setTraversal(RowTraversal traversal) {
        this.traversal = traversal;
    }

    public RowTraversal getTraversal() {
        return traversal;
    }

    public List<AggregatorValue[]> getTopGroupValues() {
        return topGroupValues;
    }

    public void setTopGroupValues(List<AggregatorValue[]> topGroupValues) {
        this.topGroupValues = topGroupValues;
    }
}
