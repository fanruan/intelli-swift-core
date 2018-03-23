package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.result.ChildMap;

public class SwiftBIGroupNode implements BIGroupNode {

    private int deep;
    private Object data;
    private ChildMap<BIGroupNode> childMap = new ChildMap<BIGroupNode>();
    private Number[] summaryValue;
    private BIGroupNode parent;
    private BIGroupNode sibling;

    public SwiftBIGroupNode(int deep, Object data, Number[] summaryValue) {
        this.deep = deep;
        this.data = data;
        this.summaryValue = summaryValue;
    }

    public void addChild(BIGroupNode child) {
        if (getLastChild() != null) {
            ((SwiftBIGroupNode) getLastChild()).sibling = child;
        }
        childMap.put(child.getData(), child);
        ((SwiftBIGroupNode) child).parent = this;
    }

    @Override
    public BIGroupNode getChild(int i) {
        return childMap.get(i);
    }

    @Override
    public BIGroupNode getChild(Object value) {
        return childMap.get(value);
    }

    @Override
    public BIGroupNode getLastChild() {
        return childMap.size() == 0 ? null : childMap.get(childMap.size() - 1);
    }

    @Override
    public BIGroupNode getFirstChild() {
        return childMap.size() == 0 ? null : childMap.get(0);
    }

    @Override
    public int getChildLength() {
        return childMap.size();
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
        return deep;
    }

    @Override
    public int getTotalLength() {
        int count = 1;
        for (int i = 0; i < childMap.size(); i++) {
            BIGroupNode node = childMap.get(i);
            count += node.getTotalLength();
        }
        return Math.max(count, 1);
    }

    @Override
    public int getTotalLengthWithSummary() {
        int count = 1;
        for (int i = 0; i < childMap.size(); i++) {
            BIGroupNode node = childMap.get(i);
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
    public ResultType getResultType() {
        return ResultType.BIGROUP;
    }

    @Override
    public void setSummaryValue(int index, Number value) {
        summaryValue[index] = value;
    }

    @Override
    public Number getSummaryValue(int index) {
        return summaryValue[index];
    }

    @Override
    public Number[] getSummaryValue() {
        return summaryValue;
    }

    @Override
    public void setSummaryValue(Number[] summaryValue) {
        this.summaryValue = summaryValue;
    }
}
