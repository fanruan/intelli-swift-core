package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;

import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/22
 */
public class CustomSortGroupRule<Base> extends BaseCustomGroupRule<Base, Base> {
    public CustomSortGroupRule(List<? extends CustomGroup<Base, Base>> customGroups) {
        super(customGroups, null);
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

    public static class NumGroup extends CustomGroup<Number, Number> {
        List<Number> values;

        public NumGroup(Number name) {
            super(name);
            values = Collections.singletonList(name);
        }

        @Override
        List<Number> values() {
            return values;
        }
    }
}