package com.fr.swift.result;

import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class GroupNode<T extends GroupNode<T>> extends AbstractSwiftNode<T> {

    private static final long serialVersionUID = -538699789884622264L;
    private int depth;
    int nodeIndex = 0;
    protected Object data;
    private ChildMap<T> childMap = new ChildMap<T>();
    private int dictionaryIndex = -1;
    private boolean isGlobalIndexUpdated = false;

    public GroupNode(int depth, Object data) {
        this.depth = depth;
        this.data = data;
    }

    public GroupNode(int depth, int segmentIndex) {
        this.depth = depth;
        this.dictionaryIndex = segmentIndex;
    }

    public GroupNode() {
    }

    public int getDictionaryIndex() {
        return dictionaryIndex;
    }

    public ChildMap<T> getChildMap() {
        return childMap;
    }

    @Override
    @Deprecated
    public T getSibling() {
        return super.getSibling();
    }

    @Override
    public T getParent() {
        return super.getParent();
    }

    @Override
    public int getDepth() {
        return depth;
    }

    public void setDepth(int dedepth) {
        this.depth = dedepth;
    }

    @Override
    public void clearChildren() {
        childMap = new ChildMap<T>();
    }

    @Override
    public List<T> getChildren() {
        return childMap.getList();
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
    public void addChild(T child) {
        int siblings = getChildrenSize();
        if (siblings != 0) {
            getLastChild().sibling = child;
            // 在构造的时候设置这个没啥用，但是过滤之后，重新把节点添加进来的时候就有用了
            child.nodeIndex = getLastChild().nodeIndex + 1;
        } else {
            child.nodeIndex = 0;
        }
        childMap.put(child.getData(), child);
        child.parent = (T) this;
    }

    private T getLastChild() {
        return childMap.size() == 0 ? null : childMap.get(childMap.size() - 1);
    }

    public void setGlobalIndex(int globalIndex) {
        if (!isGlobalIndexUpdated) {
            this.dictionaryIndex = globalIndex;
            isGlobalIndexUpdated = true;
        }
    }

    public boolean isGlobalIndexUpdated() {
        return isGlobalIndexUpdated;
    }

    @Override
    public T getChild(int index) {
        return childMap.get(index);
    }

    @Override
    public int getChildrenSize() {
        return childMap == null ? 0 : childMap.size();
    }

    @Override
    public int getIndex() {
        return nodeIndex;
    }

    public void setIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }
}
