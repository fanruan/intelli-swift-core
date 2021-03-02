package com.fr.swift.cloud.result;

import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class GroupNode extends AbstractSwiftNode {

    private static final long serialVersionUID = -538699789884622264L;
    private int depth;
    private int nodeIndex = 0;
    private Object data;
    private ChildMap<SwiftNode> childMap = new ChildMap<SwiftNode>();
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

    @Override
    public int getDictionaryIndex() {
        return dictionaryIndex;
    }

    public ChildMap<SwiftNode> getChildMap() {
        return childMap;
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
        childMap = new ChildMap<SwiftNode>();
    }

    @Override
    public List<SwiftNode> getChildren() {
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
    public void addChild(SwiftNode child) {
        int siblings = getChildrenSize();
        if (siblings != 0) {
            getLastChild().setSibling(child);
            // 在构造的时候设置这个没啥用，但是过滤之后，重新把节点添加进来的时候就有用了
            child.setIndex(getLastChild().getIndex() + 1);
        } else {
            child.setIndex(0);
        }
        childMap.put(child.getData(), child);
        child.setParent(this);
    }

    private SwiftNode getLastChild() {
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
    public SwiftNode getChild(int index) {
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

    @Override
    public void setIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }
}
