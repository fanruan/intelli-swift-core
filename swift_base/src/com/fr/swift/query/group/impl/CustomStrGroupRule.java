package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.source.core.CoreService;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/28
 * <p>
 * 字符串自定义分组规则
 */
public class CustomStrGroupRule extends BaseCustomGroupRule<String> {
    @CoreField
    private List<StringGroup> groups;

    public CustomStrGroupRule(List<StringGroup> groups, String otherGroupName) {
        super(otherGroupName);
        this.groups = groups;
    }

    @Override
    void initMap() {
        int lastIndex = groups.size() + 1;

        int dictSize = dictColumn.size();
        reverseMap = new int[dictSize];

        // 0号为null
        map.put(0, Pair.of((String) null, IntListFactory.newSingleList(0)));

        for (int i = 1; i < dictSize; i++) {
            String val = dictColumn.getValue(i);
            int index = findIndexByValue(val);

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

        compactMap(lastIndex);
    }

    private int findIndexByValue(String val) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).values.contains(val)) {
                // 有效字典序号从1开始
                return i + 1;
            }
        }
        return -1;
    }

    private int findIndexByName(String name) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).name.equals(name)) {
                // 有效字典序号从1开始
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        if (index == 0) {
            return 0;
        }

        String name = getValue(index);
        int i = findIndexByName(name);
        // 在功能传来的分组里
        if (i != -1) {
            return i;
        }
        // 有其他组，序号在最后
        if (hasOtherGroup()) {
            return groups.size() + 1;
        }

        IntList indices = map(index);
        assert indices.size() == 1;
        String val = dictColumn.getValue(indices.get(0));

        Comparator<String> c = dictColumn.getComparator();

        // 未分组的各自一组，要算全局字典偏移
        int offset = 0;
        for (StringGroup group : groups) {
            List<String> values = group.values;
            if (c.compare(values.get(0), val) < 0) {
                // 新分组全部或部分在当前值的左边
                for (String value : values) {
                    if (c.compare(value, val) < 0) {
                        offset--;
                    } else {
                        break;
                    }
                }
                offset++;
            } else {
                // 无论包含多少个旧值，都只算一组，占一个坑，把本组往后挤了一位
                offset++;
            }
        }

        return dictColumn.getGlobalIndexByIndex(dictColumn.getIndex(val)) + offset;
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CUSTOM;
    }

    public static class StringGroup implements CoreService {
        @CoreField
        String name;
        @CoreField
        List<String> values;

        public StringGroup(String name, List<String> values) {
            this.name = name;
            this.values = values;
        }

        @Override
        public Core fetchObjectCore() {
            try {
                return new CoreGenerator(this).fetchObjectCore();
            } catch (Exception ignore) {
                return Core.EMPTY_CORE;
            }
        }
    }
}