package com.fr.swift.result;

import java.util.List;

/**
 * @author pony
 * @date 2017/10/10
 * 存储聚合之后的值的树状结构
 */
public interface SwiftNode extends AggregatorValueContainer {

    Object getData();

    void setData(Object data);

    SwiftNode getChild(int index);

    void addChild(SwiftNode child);

    /**
     * 节点的兄弟节点，下一个节点，用于纵向遍历。
     *
     * @return
     */
    SwiftNode getSibling();

    void setSibling(SwiftNode sibling);

    SwiftNode getParent();

    void setParent(SwiftNode parent);

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

    List<SwiftNode> getChildren();

    void setIndex(int nodeIndex);

    int getDictionaryIndex();
}
