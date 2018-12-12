package com.fr.swift.result.qrs;

import java.io.Serializable;

/**
 * Created by lyon on 2018/11/21.
 */
public interface QueryResultSet<T extends Serializable> {

    int getFetchSize();

    /**
     * 根据这个type来转换
     *
     * @return
     */
    // TODO: 2018/11/28 这个type及这边的泛型不利于处理中间结果过程中在多种实现之间灵活切换
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
