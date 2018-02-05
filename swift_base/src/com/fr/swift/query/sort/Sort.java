package com.fr.swift.query.sort;

/**
 * Created by pony on 2017/12/11.
 */
public interface Sort {
    /**
     * 类型
     * @return
     */
    SortType getSortType();

    /**
     * 依据字段
     * @return
     */
    int getTargetIndex();
}
