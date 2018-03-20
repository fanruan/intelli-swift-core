package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/28
 * <p>
 * 字符串自定义分组规则
 */
public class CustomStrGroupRule extends BaseCustomGroupRule {
    private List<StringGroup> groups;

    public CustomStrGroupRule(List<StringGroup> groups, String otherGroupName) {
        super(otherGroupName);
        this.groups = groups;
    }

    @Override
    void initMap() {
        int lastIndex = groups.size();

        int dictSize = dictColumn.size();
        reverseMap = new int[dictSize];

        for (int i = 0; i < dictSize; i++) {
            String val = (String) dictColumn.<String>getValue(i);
            int index = findIndex(val);

            String groupName;
            if (index != -1) {
                // 在区间里
                groupName = groups.get(index).name;
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

            if (map.containsKey(index)) {
                map.get(index).getValue().add(i);
            } else {
                IntList indices = IntListFactory.createIntList();
                indices.add(i);
                map.put(index, Pair.of(groupName, indices));
                reverseMap[i] = index;
            }
        }
    }

    private void filterValidGroup() {
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
                return i;
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