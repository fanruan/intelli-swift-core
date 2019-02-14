package com.fr.swift.result;

import java.util.List;

/**
 * @author pony
 * @date 2017/10/10
 * 存储聚合之后的值的树状结构
 */
public interface SwiftNode<T extends SwiftNode> extends AggregatorValueContainer {

    Object getData();

    void setData(Object data);

    T getChild(int index);

    void addChild(T child);

    /**
     * 节点的兄弟节点，下一个节点，用于纵向遍历。
     *
     * @return
     */
    T getSibling();

    void setSibling(T sibling);

    T getParent();

    void setParent(T parent);

    int getChildrenSize();

    /**
     * 兄弟节点之间的索引, 从0开始
     *
     * @return
     */
    int getIndex();

    /**
     * 节点的深度
     *
     * @return
     */
    int getDepth();

    void clearChildren();

    List<T> getChildren();
}
