package com.fr.swift.query.group.impl;

/**
 * @author anchore
 * @date 2018/1/29
 * <p>
 * 自动分组规则 5个一组
 */
class AutoGroupRule extends BaseGroupRule {
    private static final int DEFAULT_STEP = 5;

    private int step;
    private int oldSize = dictColumn.size();

    public AutoGroupRule(int step) {
        this.step = step;
    }

    public AutoGroupRule() {
        this(DEFAULT_STEP);
    }

    @Override
    public String getGroupName(int index) {
        int[] oldIndices = map(index);
        return String.format("%s - %s",
                dictColumn.getValue(oldIndices[0]),
                dictColumn.getValue(oldIndices[oldIndices.length - 1]));
    }

    @Override
    public int[] map(int index) {
        int cmp = (index + 1) * step - oldSize;
        int[] oldIndices;
        if (cmp > 0) {
            oldIndices = new int[step - cmp];
        } else {
            oldIndices = new int[step];
        }

        for (int i = 0; i < oldIndices.length; i++) {
            oldIndices[i] = index * step + i;
        }
        return oldIndices;
    }

    @Override
    public int newSize() {
        return oldSize / step + 1;
    }
}