package com.fr.swift.query.group.impl;

/**
 * @author anchore
 * @date 2018/1/31
 * <p>
 * 不分组规则 1个一组
 */
class NoGroupRule extends BaseGroupRule {
    @Override
    public String getGroupName(int index) {
        return dictColumn.getValue(index).toString();
    }

    @Override
    public int[] map(int index) {
        return new int[]{index};
    }

    @Override
    public int newSize() {
        return dictColumn.size();
    }
}
