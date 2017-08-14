package com.fr.bi.report.result;

/**
 * Created by andrew_asa on 2017/8/12.
 */
public interface BIGroupNode extends GroupNodeSummaryContainer, BICell {

    /**
     * 获取子节点
     *
     * @return
     */
    //List<BIGroupNode> getChilds();

    ///**
    // * 设置儿子
    // *
    // * @param childs
    // */
    //void addChilds(Set<BIGroupNode> childs);

    /**
     * 获取儿子
     *
     * @param i 第几个儿子
     * @return
     */
    BIGroupNode getChild(int i);

    /**
     * 获取儿子
     *
     * @param value
     * @return
     */
    BIGroupNode getChild(Object value);

    /**
     * 获取最后一个儿子
     *
     * @return
     */
    BIGroupNode getLastChild();

    /**
     * 获取第一个儿子
     *
     * @return
     */
    BIGroupNode getFirstChild();

    /**
     * 获取儿子的数量
     *
     * @return
     */
    int getChildLength();

    ///**
    // * 设置儿子
    // *
    // * @param child
    // */
    //void addChild(BIGroupNode child);


    /**
     * 获取父节点
     *
     * @return
     */
    BIGroupNode getParent();

    ///**
    // * 设置父亲
    // *
    // * @param parent
    // */
    //void setParent(BIGroupNode parent);
}
