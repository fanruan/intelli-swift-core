package com.fr.swift.query.sort;

/**
 * @author pony
 * @date 2018/1/23
 */
abstract class AbstractSort implements Sort {
    private int targetIndex;

    AbstractSort(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public int getTargetIndex() {
        return targetIndex;
    }
}
