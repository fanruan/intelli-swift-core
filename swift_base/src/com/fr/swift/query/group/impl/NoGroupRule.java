package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

/**
 * @author anchore
 * @date 2018/1/31
 * <p>
 * 不分组规则 1个一组
 */
public class NoGroupRule extends BaseGroupRule {
    @Override
    public String getGroupName(int index) {
        return dictColumn.getValue(index).toString();
    }

    @Override
    public IntList map(int index) {
        IntList list = IntListFactory.createIntList(1);
        list.add(index);
        return list;
    }

    @Override
    public int reverseMap(int originIndex) {
        return originIndex;
    }

    @Override
    public int newSize() {
        return dictColumn.size();
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.NONE;
    }

    @Override
    void initMap() {
        // 不分组，直接用原来的映射关系
    }
}
