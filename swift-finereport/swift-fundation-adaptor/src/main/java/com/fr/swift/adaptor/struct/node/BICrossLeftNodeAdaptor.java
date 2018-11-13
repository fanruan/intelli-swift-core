package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BICrossLeftNode;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.result.XLeftNode;

/**
 * Created by Lyon on 2018/4/9.
 */
public class BICrossLeftNodeAdaptor implements BICrossLeftNode {

    private XLeftNode xLeftNode;

    public BICrossLeftNodeAdaptor(XLeftNode xLeftNode) {
        this.xLeftNode = xLeftNode;
    }

    @Override
    public Number[][] getXValue() {
        return XLeftNode.toNumber2DArray(xLeftNode.getXValue());
    }

    @Override
    public Number[] getSubValues(int index) {
        return XLeftNode.toNumberArray(xLeftNode.getValuesByTopGroupByRow(index));
    }

    @Override
    public BICrossLeftNode getFirstCrossLeftNode() {
        return new BICrossLeftNodeAdaptor(xLeftNode.getFirstXLeftNode());
    }

    @Override
    public BIGroupNode getChild(int i) {
        return new BICrossLeftNodeAdaptor(xLeftNode.getChild(i));
    }

    @Override
    public BIGroupNode getChild(Object value) {
        return new BICrossLeftNodeAdaptor(xLeftNode.getChildMap().get(value));
    }

    @Override
    public BIGroupNode getLastChild() {
        int size = xLeftNode.getChildrenSize();
        return size == 0 ? null : new BICrossLeftNodeAdaptor(xLeftNode.getChild(size - 1));
    }

    @Override
    public BIGroupNode getFirstChild() {
        return getFirstCrossLeftNode();
    }

    @Override
    public int getChildLength() {
        return xLeftNode.getChildrenSize();
    }

    @Override
    public BIGroupNode getSiblingGroupNode() {
        XLeftNode leftNode = xLeftNode.getSibling();
        return leftNode == null ? null : new BICrossLeftNodeAdaptor(leftNode);
    }

    @Override
    public BIGroupNode getParent() {
        XLeftNode p = xLeftNode.getParent();
        return p == null ? null : new BICrossLeftNodeAdaptor(p);
    }

    @Override
    public int getDeep() {
        return xLeftNode.getDepth();
    }

    @Override
    public int getTotalLength() {
        int count = 1;
        for (int i = 0; i < xLeftNode.getChildrenSize(); i++) {
            XLeftNode node = xLeftNode.getChild(i);
            BICrossLeftNodeAdaptor adaptor = new BICrossLeftNodeAdaptor(node);
            count += adaptor.getTotalLength();
        }
        return Math.max(count, 1);
    }

    @Override
    public int getTotalLengthWithSummary() {
        int count = 1;
        for (int i = 0; i < xLeftNode.getChildrenSize(); i++) {
            XLeftNode node = xLeftNode.getChild(i);
            BICrossLeftNodeAdaptor adaptor = new BICrossLeftNodeAdaptor(node);
            count += adaptor.getTotalLengthWithSummary();
        }
        if (getChildLength() <= 1) {
            count -= 1;
        }
        return Math.max(count, 1);
    }

    @Override
    public Object getData() {
        return xLeftNode.getData();
    }

    @Override
    public void setSummaryValue(int index, Number value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Number getSummaryValue(int index) {
        return null;
    }

    @Override
    public Number[] getSummaryValue() {
        return new  Number[0];
    }

    @Override
    public void setSummaryValue(Number[] summaryValue) {
        throw new UnsupportedOperationException();
    }
}
