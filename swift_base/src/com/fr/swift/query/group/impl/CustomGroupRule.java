package com.fr.swift.query.group.impl;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.util.Crasher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/2/28
 * <p>
 * 字符串自定义分组规则
 */
public class CustomGroupRule extends BaseGroupRule {
    private List<StringGroup> groups;

    private Map<Integer, IntList> map = new HashMap<Integer, IntList>();

    public CustomGroupRule(List<StringGroup> groups) {
        super();
        this.groups = groups;
    }

    @Override
    public String getGroupName(int index) {
        return groups.get(index).name;
    }

    @Override
    public IntList map(int index) {
        return map.get(index);
    }

    @Override
    void initMap() {
        for (int i = 0; i < dictColumn.size(); i++) {
            String val = (String) dictColumn.<String>getValue(i);
            int index = findIndex(val);

            if (map.containsKey(index)) {
                map.get(index).add(i);
            } else {
                IntList indices = IntListFactory.createIntList();
                indices.add(i);
                map.put(index, indices);
            }
        }
    }

    private int findIndex(String val) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).contains(val)) {
                return i;
            }
        }
        return Crasher.crash("value not found");
    }

    @Override
    public int newSize() {
        return groups.size();
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