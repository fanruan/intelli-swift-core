package com.fr.swift.query.sort;

/**
 * @author pony
 * @date 2018/1/23
 */
public class AscSort extends AbstractSort {
    public AscSort(int targetIndex, String targetFieldId) {
        super(targetIndex, targetFieldId);
    }

    @Override
    public SortType getSortType() {
        return SortType.ASC;
    }
}
