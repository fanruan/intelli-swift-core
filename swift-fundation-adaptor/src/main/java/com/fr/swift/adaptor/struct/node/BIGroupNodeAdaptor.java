package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.result.GroupNode;

/**
 * Created by Lyon on 2018/4/8.
 */
public class BIGroupNodeAdaptor implements BIGroupNode {

    private GroupNode node;

    public BIGroupNodeAdaptor(GroupNode node) {
        this.node = node;
    }

    @Override
    public BIGroupNode getChild(int i) {
        return node.getChild(i) == null ? null : new BIGroupNodeAdaptor(node.getChild(i));
    }

    @Override
    public BIGroupNode getChild(Object value) {
        // TODO: 2018/5/10 这个接口要是在功能那边确认没用，可以去掉了
        GroupNode n = (GroupNode) node.getChildMap().get(value);
        return n == null ? null : new BIGroupNodeAdaptor(n);
    }

    @Override
    public BIGroupNode getLastChild() {
        GroupNode n = node.getChildrenSize() == 0 ? null : node.getChild(node.getChildrenSize() - 1);
        return n == null ? null : new BIGroupNodeAdaptor(n);
    }

    @Override
    public BIGroupNode getFirstChild() {
        GroupNode n = node.getChildrenSize() == 0 ? null : node.getChild(0);
        return n == null ? null : new BIGroupNodeAdaptor(n);
    }

    @Override
    public int getChildLength() {
        return node.getChildrenSize();
    }

    @Override
    public BIGroupNode getSiblingGroupNode() {
        GroupNode n = node.getSibling();
        return n == null ? null : new BIGroupNodeAdaptor(n);
    }

    @Override
    public BIGroupNode getParent() {
        return node.getParent() == null ? null : new BIGroupNodeAdaptor(node.getParent());
    }

    @Override
    public int getDeep() {
        return node.getDepth();
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
        return node.getData();
    }

    @Override
    public void setSummaryValue(int index, Number value) {
        node.setAggregatorValue(index, new DoubleAmountAggregatorValue(value.doubleValue()));
    }

    @Override
    public Number getSummaryValue(int index) {
        AggregatorValue value = node.getAggregatorValue(index);
        return value == null ? null : (Number) value.calculateValue();
    }

    @Override
    public Number[] getSummaryValue() {
        Number[] values = new Number[node.getAggregatorValue().length];
        AggregatorValue[] aggregatorValues = node.getAggregatorValue();
        for (int i = 0; i < values.length; i++) {
            AggregatorValue value = aggregatorValues[i];
            values[i] = value == null ? null : (Number) value.calculateValue();
        }
        return values;
    }

    @Override
    public void setSummaryValue(Number[] summaryValue) {
        AggregatorValue[] values = new AggregatorValue[summaryValue.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = new DoubleAmountAggregatorValue(summaryValue[i].doubleValue());
        }
    }
}
