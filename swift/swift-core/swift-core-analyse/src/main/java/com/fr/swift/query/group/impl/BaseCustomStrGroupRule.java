package com.fr.swift.query.group.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.compare.Comparators;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/5/8
 */
abstract class BaseCustomStrGroupRule<Base> extends BaseCustomGroupRule<Base, String> {
    /**
     * 是否排序
     */
    private boolean sorted;

    BaseCustomStrGroupRule(List<? extends CustomGroup<Base, String>> customGroups, String otherGroupName, boolean sorted) {
        super(customGroups, otherGroupName);
        this.sorted = sorted;
    }

    @Override
    boolean hasOtherGroup() {
        return StringUtils.isNotEmpty(otherGroupName);
    }

    @Override
    Comparator<Entry<Integer, Pair<String, IntList>>> getComparator() {
        return sorted ? new Comparator<Entry<Integer, Pair<String, IntList>>>() {
            @Override
            public int compare(Entry<Integer, Pair<String, IntList>> o1, Entry<Integer, Pair<String, IntList>> o2) {
                return Comparators.PINYIN_ASC.compare(o1.getValue().getKey(), o2.getValue().getKey());
            }
        } : super.getComparator();
    }
}