package com.fr.swift.result.qrs;

/**
 * @author lyon
 * @date 2018/11/21
 */
public interface QueryResultSet<T> {

    int getFetchSize();

    /**
     * 根据这个type来转换
     * TODO: 2018/11/28 这个type及这边的泛型不利于处理中间结果过程中在多种实现之间灵活切换
     *
     * @return
     */
    DSType type();

    /**
     * 获取一页数据
     *
     * @return
     */
    T getPage();

    /**
     * 是否有下一页
     *
     * @return
     */
    boolean hasNextPage();
}
