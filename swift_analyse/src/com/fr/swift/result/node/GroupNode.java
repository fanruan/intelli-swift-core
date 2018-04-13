package com.fr.swift.result.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.AbstractSwiftNode;
import com.fr.swift.result.ChildMap;

/**
 * Created by Lyon on 2018/4/4.
 */
public class GroupNode<T extends GroupNode> extends AbstractSwiftNode<T> {

    protected int deep;
    protected Object data;
    protected ChildMap<T> childMap = new ChildMap<T>();

    public GroupNode(int sumLength, int deep, Object data) {
        super(sumLength);
        this.deep = deep;
        this.data = data == null ? "" : data;
    }

    public GroupNode(int deep, Object data) {
        this.deep = deep;
        this.data = data == null ? "" : data;
    }

    protected GroupNode() {}

    public ChildMap<T> getChildMap() {
        return childMap;
    }

    @Override
    public T getSibling() {
        return super.getSibling();
    }

    @Override
    public T getParent() {
        return super.getParent();
    }

    @Override
    public int getDeep() {
        return deep;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public T getChild(int index) {
        return childMap.get(index);
    }

    @Override
    public void addChild(T child) {
        if (getLastChild() != null) {
            getLastChild().sibling = child;
        }
        childMap.put(child.getData(), child);
        child.parent = this;
    }

    private GroupNode getLastChild() {
        return childMap.size() == 0 ? null : childMap.get(childMap.size() - 1);
    }

    @Override
    public int getChildrenSize() {
        return childMap.size();
    }

    @Override
    public int getIndex() {
        return 0;
    }

    public static Number[] toNumberArray(AggregatorValue[] values) {
        Number[] result = new Number[values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (Number) values[i].calculateValue();
        }
        return result;
    }
}
