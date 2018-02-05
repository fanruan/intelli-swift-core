package com.fr.swift.query.group.impl;

/**
 * @author anchore
 * @date 2018/1/29
 * <p>
 * 自动分组规则 5个一组
 */
class AutoGroupRule extends BaseGroupRule {
    private static final int DEFAULT_SIZE = 5;
    private int oldSize = dictColumn.size();

    @Override
    public String getGroupName(int index) {
        int[] oldIndices = map(index);
        return String.format("%s - %s",
                dictColumn.getValue(oldIndices[0]),
                dictColumn.getValue(oldIndices[oldIndices.length - 1]));
    }

    @Override
    public int[] map(int index) {
        int cmp = (index + 1) * DEFAULT_SIZE - oldSize;
        int[] oldIndices;
        if (cmp > 0) {
            oldIndices = new int[DEFAULT_SIZE - cmp];
        } else {
            oldIndices = new int[DEFAULT_SIZE];
        }

        for (int i = 0; i < oldIndices.length; i++) {
            oldIndices[i] = index * DEFAULT_SIZE + i;
        }
        return oldIndices;
    }

    @Override
    public int newSize() {
        return oldSize / DEFAULT_SIZE + 1;
    }
}
