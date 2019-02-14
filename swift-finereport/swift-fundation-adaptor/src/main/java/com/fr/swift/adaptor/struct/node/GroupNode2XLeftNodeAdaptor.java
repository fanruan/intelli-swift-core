package com.fr.swift.adaptor.struct.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.XLeftNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/5/11.
 */
public class GroupNode2XLeftNodeAdaptor extends XLeftNode {

    private GroupNode node;

    public GroupNode2XLeftNodeAdaptor(GroupNode node) {
        this.node = node;
        AggregatorValue[] values = node.getAggregatorValue();
        List<AggregatorValue[]> valuesList = new ArrayList<AggregatorValue[]>(1);
        valuesList.add(values);
        setXValues(valuesList);
    }

    @Override
    public XLeftNode getChild(int index) {
        return new GroupNode2XLeftNodeAdaptor(node.getChild(index));
    }

    @Override
    public int getChildrenSize() {
        return node.getChildrenSize();
    }

    @Override
    public XLeftNode getFirstXLeftNode() {
        return node.getChildrenSize() == 0 ? null : new GroupNode2XLeftNodeAdaptor(node.getChild(0));
    }

    @Override
    public XLeftNode getSibling() {
        return node.getSibling() == null ? null : new GroupNode2XLeftNodeAdaptor(node.getSibling());
    }

    @Override
    public XLeftNode getParent() {
        return node.getParent() == null ? null : new GroupNode2XLeftNodeAdaptor(node.getParent());
    }

    @Override
    public int getDepth() {
        return node.getDepth();
    }

    @Override
    public Object getData() {
        return node.getData();
    }
}
