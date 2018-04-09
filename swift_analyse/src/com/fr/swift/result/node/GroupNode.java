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

    // TODO: 2018/4/8 SwiftNode接口有待调整。当前所有聚合结构都是按行保存的，只有聚合完处理计算指标之前才转为node结构
    public ChildMap<GroupNode> getChildMap() {
        return childMap;
    }

    public void setSummaryValue(int index, Number value) {
        values[index] = value;
    }

    public Number getSummaryValue(int index) {
        return values[index];
    }

    public Number[] getSummaryValue() {
        return values;
    }

    public void setSummaryValue(Number[] summaryValue) {
        this.values = summaryValue;
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
}
