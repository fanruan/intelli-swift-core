package com.fr.swift.result;

import com.fr.swift.result.page.NodeRange;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/4.
 */
public class GroupNode<T extends GroupNode> extends AbstractSwiftNode<T> implements Iterable<T> {

    protected int deep;
    protected int nodeIndex = 0;
    protected Object data;
    protected ChildMap<T> childMap = new ChildMap<T>();
    protected int dictionaryIndex = -1;
    protected NodeRange nodeRange = null;
    private boolean isGlobalIndexUpdated = false;

    public GroupNode(int deep, Object data) {
        this.deep = deep;
        this.data = data;
    }

    public GroupNode(int deep, int segmentIndex) {
        this.deep = deep;
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

    public void setIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
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
    public void addChild(T child) {
        T lastChild = getLastChild();
        if (lastChild != null) {
            lastChild.sibling = child;
            // 在构造的时候设置这个没啥用，但是过滤之后，重新把节点添加进来的时候就有用了
            child.nodeIndex = lastChild.nodeIndex + 1;
        }
        childMap.put(child.getData(), child);
        child.parent = this;
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

    /**
     * 用于对结果集进行分页，改变getChild(index)和getChildrenSize()方法的默认行为
     * 恢复默认行为设置null就可以了
     *
     * @param range
     */
    public void setNodeRange(NodeRange range) {
        if (nodeRange != null) {
            if (nodeRange.getStartIndexIncluded() == NodeRange.UNDEFINED) {
                nodeRange.setStartIndexIncluded(range.getStartIndexIncluded());
            }
            if (nodeRange.getEndIndexIncluded() == NodeRange.UNDEFINED) {
                nodeRange.setEndIndexIncluded(range.getEndIndexIncluded());
            }
        } else {
            nodeRange = range;
        }
    }

    @Override
    public T getChild(int index) {
        if (nodeRange == null || nodeRange.getStartIndexIncluded() == NodeRange.UNDEFINED) {
            return childMap.get(index);
        }
        return childMap.get(index + nodeRange.getStartIndexIncluded());
    }

    @Override
    public int getChildrenSize() {
        if (nodeRange == null) {
            return childMap == null ? 0 : childMap.size();
        }
        if (nodeRange.getStartIndexIncluded() == NodeRange.UNDEFINED) {
            return nodeRange.getEndIndexIncluded() + 1;
        }
        if (nodeRange.getEndIndexIncluded() == NodeRange.UNDEFINED) {
            return childMap.size() - nodeRange.getStartIndexIncluded();
        }
        return nodeRange.getEndIndexIncluded() - nodeRange.getStartIndexIncluded() + 1;
    }

    @Override
    public int getIndex() {
        return nodeIndex;
    }

    @Override
    public Iterator<T> iterator() {
        return getChildren().iterator();
    }
}
