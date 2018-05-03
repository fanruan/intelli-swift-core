package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;

import java.util.List;

/**
 * @author anchore
 * @date 2018/4/22
 */
public class CustomSortGroupRule<Base> extends BaseCustomGroupRule<Base, Base> {
    public CustomSortGroupRule(List<? extends CustomGroup<Base, Base>> customGroups, Base otherGroupName) {
        super(customGroups, otherGroupName);
    }

    @Override
    boolean hasOtherGroup() {
        return false;
    }

    @Override
    Base format(Base val) {
        return val;
    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        return index;
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CUSTOM_SORT;
    }
}