package com.fr.swift.query.sort;

/**
 * @author pony
 * @date 2018/1/23
 */
abstract class AbstractSort implements Sort {
    private int targetIndex;

    private String targetFieldId;
    AbstractSort(int targetIndex) {
        this.targetIndex = targetIndex;
    }
    AbstractSort(int targetIndex, String targetFieldId) {
        this.targetIndex = targetIndex;
        this.targetFieldId = targetFieldId;
    }

    @Override
    public int getTargetIndex() {
        return targetIndex;
    }

    @Override
    public String getTargetFieldId() {
        return targetFieldId;
    }
}
