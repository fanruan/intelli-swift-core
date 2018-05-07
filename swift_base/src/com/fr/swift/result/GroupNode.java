package com.fr.swift.result;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class GroupNode<T extends GroupNode> extends AbstractSwiftNode<T> implements Iterable<T> {

    protected int deep;
    protected Object data;
    protected ChildMap<T> childMap = new ChildMap<T>();
    protected int dictionaryIndex = -1;
    private boolean isGlobalIndexUpdated = false;

    public GroupNode(int deep, Object data) {
        this.deep = deep;
        this.data = data;
    }

    public GroupNode(int deep, int segmentIndex) {
        this.deep = deep;
        this.dictionaryIndex = segmentIndex;
    }

    public void setGlobalIndex(int globalIndex) {
        if (!isGlobalIndexUpdated) {
            this.dictionaryIndex = globalIndex;
            isGlobalIndexUpdated = true;
        }
    }

    public int getDictionaryIndex() {
        return dictionaryIndex;
    }

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
    public int getDepth() {
        return deep;
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
        return childMap == null ? 0 : childMap.size();
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Iterator<T> iterator() {
        return getChildren().iterator();
    }
}
