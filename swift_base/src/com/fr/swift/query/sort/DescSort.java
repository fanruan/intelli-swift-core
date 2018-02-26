package com.fr.swift.query.sort;

/**
 * @author pony
 * @date 2018/1/23
 */
public class DescSort extends AbstractSort {
    public DescSort(int targetIndex) {
        super(targetIndex);
    }

    @Override
    public SortType getSortType() {
        return SortType.DESC;
    }
}
