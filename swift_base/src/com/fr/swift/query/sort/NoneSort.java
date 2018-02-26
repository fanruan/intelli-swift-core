package com.fr.swift.query.sort;

/**
 * @author pony
 * @date 2018/1/23
 */
public class NoneSort implements Sort {
    @Override
    public SortType getSortType() {
        return SortType.NONE;
    }

    @Override
    public int getTargetIndex() {
        return 0;
    }
}
