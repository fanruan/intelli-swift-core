package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.result.ChildMap;

public class SwiftBIGroupNode implements BIGroupNode {

    private ChildMap<BIGroupNode> childMap;
    private Number[] summaryValue;

    public SwiftBIGroupNode(ChildMap<BIGroupNode> childMap, Number[] summaryValue) {
        this.childMap = childMap;
        this.summaryValue = summaryValue;
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
        return childMap.get(childMap.size() - 1);
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
        return null;
    }

    @Override
    public BIGroupNode getParent() {
        return null;
    }

    @Override
    public int getDeep() {
        return 0;
    }

    @Override
    public int getTotalLength() {
        return 0;
    }

    @Override
    public int getTotalLengthWithSummary() {
        return 0;
    }

    @Override
    public Object getData() {
        return null;
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
