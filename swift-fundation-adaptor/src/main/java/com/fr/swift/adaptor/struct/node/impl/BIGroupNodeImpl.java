package com.fr.swift.adaptor.struct.node.impl;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.GroupNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/5/15.
 */
public class BIGroupNodeImpl implements BIGroupNode {

    private int depth;
    private int index;
    private Object data;
    private BIGroupNode sibling;
    private BIGroupNode parent;
    private Number[] values;
    private List<BIGroupNode> children;

    public BIGroupNodeImpl(boolean isInOrder, GroupNode node) {
        this.depth = node.getDepth();
        this.data = node.getData();
        this.values = getValues(node);
        this.index = node.getIndex();
        this.children = isInOrder ? new ArrayList<BIGroupNode>() : new ReverseGettingList();
    }

    private Number[] getValues(GroupNode node) {
        Number[] values = new Number[node.getAggregatorValue().length];
        AggregatorValue[] aggregatorValues = node.getAggregatorValue();
        for (int i = 0; i < values.length; i++) {
            AggregatorValue value = aggregatorValues[i];
            values[i] = value == null ? null : (Number) value.calculateValue();
        }
        return values;
    }

    public int getIndex() {
        return index;
    }

    public void addChild(BIGroupNodeImpl child) {
        BIGroupNodeImpl last = (BIGroupNodeImpl) getLastChild();
        if (last != null) {
            last.sibling = child;
        }
        children.add(child);
        child.parent = this;
    }

    @Override
    public BIGroupNode getChild(int i) {
        return children.get(i);
    }

    @Override
    public BIGroupNode getChild(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BIGroupNode getLastChild() {
        return children.isEmpty() ? null : children.get(children.size() - 1);
    }

    @Override
    public BIGroupNode getFirstChild() {
        return children.isEmpty() ? null : children.get(0);
    }

    @Override
    public int getChildLength() {
        return children.size();
    }

    @Override
    public BIGroupNode getSiblingGroupNode() {
        return sibling;
    }

    @Override
    public BIGroupNode getParent() {
        return parent;
    }

    @Override
    public int getDeep() {
        return depth;
    }

    @Override
    public int getTotalLength() {
        int count = 0;
        for (int i = 0; i < getChildLength(); i++) {
            BIGroupNode node = getChild(i);
            count += node.getTotalLength();
        }
        return Math.max(count, 1);
    }

    @Override
    public int getTotalLengthWithSummary() {
        int count = 1;
        for (int i = 0; i < getChildLength(); i++) {
            BIGroupNode node = getChild(i);
            count += node.getTotalLengthWithSummary();
        }
        if (getChildLength() <= 1) {
            count -= 1;
        }
        return Math.max(count, 1);
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setSummaryValue(int index, Number value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Number getSummaryValue(int index) {
        return values[index];
    }

    @Override
    public Number[] getSummaryValue() {
        return values;
    }

    @Override
    public void setSummaryValue(Number[] summaryValue) {
        throw new UnsupportedOperationException();
    }

    private static class ReverseGettingList extends ArrayList<BIGroupNode> {
        @Override
        public BIGroupNode get(int index) {
            return super.get(size() - index - 1);
        }
    }
}
