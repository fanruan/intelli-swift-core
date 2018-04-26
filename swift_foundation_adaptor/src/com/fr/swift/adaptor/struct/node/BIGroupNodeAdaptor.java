package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.result.node.ChildMap;
import com.fr.swift.result.node.GroupNode;

/**
 * Created by Lyon on 2018/4/8.
 */
public class BIGroupNodeAdaptor implements BIGroupNode {

    private GroupNode node;
    private ChildMap<GroupNode> childMap;

    public BIGroupNodeAdaptor(GroupNode node) {
        this.node = node;
        this.childMap = node.getChildMap() == null ? new ChildMap<GroupNode>() : node.getChildMap();
    }

    @Override
    public BIGroupNode getChild(int i) {
        return node.getChild(i) == null ? null : new BIGroupNodeAdaptor(node.getChild(i));
    }

    @Override
    public BIGroupNode getChild(Object value) {
        GroupNode n = childMap.get(value);
        return n == null ? null : new BIGroupNodeAdaptor(n);
    }

    @Override
    public BIGroupNode getLastChild() {
        GroupNode n = childMap.size() == 0 ? null : childMap.get(childMap.size() - 1);
        return n == null ? null : new BIGroupNodeAdaptor(n);
    }

    @Override
    public BIGroupNode getFirstChild() {
        GroupNode n = childMap.size() == 0 ? null : childMap.get(0);
        return n == null ? null : new BIGroupNodeAdaptor(n);
    }

    @Override
    public int getChildLength() {
        return childMap.size();
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
        return node.getDeep();
    }

    @Override
    public int getTotalLength() {
        int count = 1;
        for (int i = 0; i < childMap.size(); i++) {
            GroupNode node = childMap.get(i);
            BIGroupNodeAdaptor adaptor = new BIGroupNodeAdaptor(node);
            count += adaptor.getTotalLength();
        }
        return Math.max(count, 1);
    }

    @Override
    public int getTotalLengthWithSummary() {
        int count = 1;
        for (int i = 0; i < childMap.size(); i++) {
            GroupNode node = childMap.get(i);
            BIGroupNodeAdaptor adaptor = new BIGroupNodeAdaptor(node);
            count += adaptor.getTotalLengthWithSummary();
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
        Double value = node.getAggregatorValue(index).calculate();
        return Double.isNaN(value) ? null : value;
    }

    @Override
    public Number[] getSummaryValue() {
        Number[] values = new Number[node.getAggregatorValue().length];
        for (int i = 0; i < values.length; i++) {
            Double value = node.getAggregatorValue(i).calculate();
            values[i] = Double.isNaN(value) ? null : value;
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
