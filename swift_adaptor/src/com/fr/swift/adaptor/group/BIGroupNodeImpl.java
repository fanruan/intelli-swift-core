package com.fr.swift.adaptor.group;

import com.finebi.conf.structure.result.table.BIGroupNode;

/**
 * @author anchore
 * @date 2018/2/26
 */
public class BIGroupNodeImpl implements BIGroupNode {
    @Override
    public BIGroupNode getChild(int i) {
        return null;
    }

    @Override
    public BIGroupNode getChild(Object value) {
        return null;
    }

    @Override
    public BIGroupNode getLastChild() {
        return null;
    }

    @Override
    public BIGroupNode getFirstChild() {
        return null;
    }

    @Override
    public int getChildLength() {
        return 0;
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
        return null;
    }

    @Override
    public void setSummaryValue(int index, Number value) {

    }

    @Override
    public Number getSummaryValue(int index) {
        return null;
    }

    @Override
    public Number[] getSummaryValue() {
        return new Number[0];
    }

    @Override
    public void setSummaryValue(Number[] summaryValue) {

    }
}