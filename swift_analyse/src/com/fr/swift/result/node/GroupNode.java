package com.fr.swift.result.node;

import com.fr.swift.result.AbstractSwiftNode;
import com.fr.swift.result.ChildMap;

/**
 * Created by Lyon on 2018/4/4.
 */
public class GroupNode extends AbstractSwiftNode<GroupNode> {

    private int deep;
    private Object data;
    private Number[] values;
    private ChildMap<GroupNode> childMap = new ChildMap<GroupNode>();

    public GroupNode(int sumLength, int deep, Object data, Number[] values) {
        super(sumLength);
        this.deep = deep;
        this.data = data;
        this.values = values;
    }

    public Number[] getValues() {
        return values;
    }

    public void setValues(Number[] values) {
        this.values = values;
    }

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
    public GroupNode getChild(int index) {
        return childMap.get(index);
    }

    @Override
    public void addChild(GroupNode child) {
        if (getLastChild() != null) {
            sibling = child;
        }
        childMap.put(child.getData(), child);
        child.parent = this;
    }

    private GroupNode getLastChild() {
        return childMap.size() == 0 ? null : childMap.get(childMap.size() - 1);
    }

    @Override
    public int getChildrenSize() {
        return 0;
    }

    @Override
    public int getIndex() {
        return 0;
    }
}
