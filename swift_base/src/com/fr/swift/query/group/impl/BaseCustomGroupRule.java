package com.fr.swift.query.group.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/3/2
 */
abstract class BaseCustomGroupRule<Base> extends BaseGroupRule<Base, String> {
    /**
     * 新分组序号 -> (新分组名, 旧分组序号)
     */
    Map<Integer, Pair<String, IntList>> map = new HashMap<Integer, Pair<String, IntList>>();
    /**
     * 旧值序号 -> 新值序号
     */
    int[] reverseMap;
    @CoreField
    String otherGroupName;

    BaseCustomGroupRule(String otherGroupName) {
        this.otherGroupName = otherGroupName;
    }

    @Override
    public String getValue(int index) {
        return map.get(index).getKey();
    }

    @Override
    public IntList map(int index) {
        return map.get(index).getValue();
    }

    @Override
    public int reverseMap(int originIndex) {
        return reverseMap[originIndex];
    }

    @Override
    public int newSize() {
        return map.size();
    }

    boolean hasOtherGroup() {
        return StringUtils.isNotEmpty(otherGroupName);
    }

    void internalMap(int oldIndex, int newIndex, String groupName) {
        if (map.containsKey(newIndex)) {
            map.get(newIndex).getValue().add(oldIndex);
        } else {
            IntList indices = IntListFactory.createIntList();
            indices.add(oldIndex);
            map.put(newIndex, Pair.of(groupName, indices));
        }
        
        reverseMap[oldIndex] = newIndex;
    }
}