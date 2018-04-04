package com.fr.swift.query.group.impl;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.query.group.CustomGroupRule;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
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
abstract class BaseCustomGroupRule<Base> extends BaseGroupRule implements CustomGroupRule<Base, String> {
    DictionaryEncodedColumn<Base> dictColumn;

    /**
     * 初始化映射关系
     */
    abstract void initMap();

    @Override
    public void setOriginDict(DictionaryEncodedColumn<Base> dict) {
        this.dictColumn = dict;
        initMap();
    }

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
    public int getIndex(Object val) {
        for (int i = 0, size = newSize(); i < size; i++) {
            if (ComparatorUtils.equals(getValue(i), val)) {
                return i;
            }
        }
        return -1;
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

    @Override
    public boolean hasOtherGroup() {
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