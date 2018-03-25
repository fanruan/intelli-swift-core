package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

/**
 * @author anchore
 * @date 2018/1/31
 * <p>
 * 不分组规则 1个一组
 * 两种情况：普通列、日期子列
 */
public class NoGroupRule<Base> extends BaseGroupRule<Base, Base> {
    private GroupType type;

    public NoGroupRule() {
        this(GroupType.NONE);
    }

    public NoGroupRule(GroupType type) {
        this.type = type;
    }

    @Override
    public Base getValue(int index) {
        return dictColumn.getValue(index);
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
        return type;
    }

    @Override
    void initMap() {
        // 不分组，直接用原来的映射关系
    }
}
