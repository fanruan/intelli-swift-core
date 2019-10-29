package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.structure.array.IntList;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/28
 * <p>
 * 字符串自定义分组规则
 */
public class CustomStrGroupRule extends BaseCustomStrGroupRule<String> {
    public CustomStrGroupRule(List<? extends CustomGroup<String, String>> groups, String otherGroupName, boolean sorted) {
        super(groups, otherGroupName, sorted);
    }

    @Override
    String format(String val) {
        return val;
    }

    private int findIndexByName(String name) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getName().equals(name)) {
                // 有效字典序号从1开始
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        // fixme 支持分组排序后，数据不准确了，要加个query查下
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
        for (CustomGroup<String, String> group : groups) {
            List<String> values = group.values();
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

    public static class StringGroup extends CustomGroup<String, String> {
        @CoreField
        List<String> values;

        public StringGroup(String name, List<String> values) {
            super(name);
            this.values = values;
        }

        @Override
        List<String> values() {
            return values;
        }
    }
}