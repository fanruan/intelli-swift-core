package com.fr.swift.result;

/**
 * @author yee
 * @date 2018-12-25
 */
public interface Pagination<T> {
    /**
     * 获取一页数据，类似buffer的作用
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
