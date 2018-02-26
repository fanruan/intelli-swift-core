package com.fr.swift.query.sort;

/**
 * @author pony
 * @date 2017/12/11
 */
public interface Sort {
    /**
     * 类型
     *
     * @return
     */
    SortType getSortType();

    /**
     * 依据字段
     *
     * @return
     */
    int getTargetIndex();
}
