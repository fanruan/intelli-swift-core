package com.fr.swift.result.node;

import com.fr.swift.result.AbstractSwiftNode;
import com.fr.swift.result.ChildMap;

import java.util.List;

/**
 * Created by pony on 2017/12/8.
 */
public abstract class IndexNode extends AbstractSwiftNode<IndexNode> {
    private static Object NULL_DATA = new Object();
    protected Object data = NULL_DATA;
    private ChildMap<IndexNode> children;

    public IndexNode(int sumLength) {
        super(sumLength);
    }

    @Override
    public Object getData() {
        if (data == NULL_DATA) {
            initDataByIndex();
        }
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public IndexNode getChild(int index) {
        return getChildren().get(index);
    }

    @Override
    public void addChild(IndexNode child) {
        getChildMap().put(child.createKey(), child);
        child.setParent(this);
        if (getChildren().size() != 1) {
            // 设置兄弟节点，方便纵向遍历
            getChildren().get(child.getIndex() - 1).setSibling(child);
        }
    }

    protected abstract void initDataByIndex();


    @Override
    public int getChildrenSize() {
        return children == null ? 0 : getChildren().size();
    }


    @Override
    public int getIndex() {
        return getParent().getChildMap().getIndex(createKey());
    }

    protected abstract Object createKey();

    private ChildMap<IndexNode> getChildMap() {
        if (children == null) {
            children = new ChildMap<IndexNode>();
        }
        return children;
    }

    @Override
    public List<IndexNode> getChildren() {
        return getChildMap().getList();
    }

    @Override
    public void clearChildren() {
        children = new ChildMap<IndexNode>();
    }
}
