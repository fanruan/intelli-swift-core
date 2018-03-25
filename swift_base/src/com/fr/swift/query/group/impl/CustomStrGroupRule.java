package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;

import java.util.Iterator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/28
 * <p>
 * 字符串自定义分组规则
 */
public class CustomStrGroupRule extends BaseCustomGroupRule<String> {
    private List<StringGroup> groups;

    public CustomStrGroupRule(List<StringGroup> groups, String otherGroupName) {
        super(otherGroupName);
        this.groups = groups;
    }

    @Override
    void initMap() {
        filterInvalidGroup();

        int lastIndex = groups.size() + 1;

        int dictSize = dictColumn.size();
        reverseMap = new int[dictSize];

        for (int i = 1; i < dictSize; i++) {
            String val = dictColumn.<String>getValue(i);
            int index = findIndex(val);

            String groupName;
            if (index != -1) {
                // 在区间里
                groupName = groups.get(index - 1).name;
            } else {
                if (hasOtherGroup()) {
                    // 有其他组，则全部分到其他
                    index = lastIndex;
                    groupName = otherGroupName;
                } else {
                    // 不在区间里，又没有其他分组，则单独为一组
                    index = lastIndex++;
                    groupName = val;
                }
            }

            internalMap(i, index, groupName);
        }
    }

    private void filterInvalidGroup() {
        Iterator<StringGroup> itr = groups.iterator();
        while (itr.hasNext()) {
            StringGroup sg = itr.next();
            if (sg.values.isEmpty()) {
                itr.remove();
            }
        }
    }

    private int findIndex(String val) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).contains(val)) {
                // 有效字典序号从1开始
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CUSTOM;
    }

    public static class StringGroup {
        String name;
        List<String> values;

        public StringGroup(String name, List<String> values) {
            this.name = name;
            this.values = values;
        }

        boolean contains(String o) {
            return values.contains(o);
        }
    }
}