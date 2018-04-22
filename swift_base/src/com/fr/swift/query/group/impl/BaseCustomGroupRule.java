package com.fr.swift.query.group.impl;

import com.fr.general.ComparatorUtils;
import com.fr.swift.query.group.CustomGroupRule;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.source.core.CoreService;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/3/2
 */
abstract class BaseCustomGroupRule<Base, Derive> extends BaseGroupRule implements CustomGroupRule<Base, Derive> {
    DictionaryEncodedColumn<Base> dictColumn;
    @CoreField
    Derive otherGroupName;
    @CoreField
    List<CustomGroup<Base, Derive>> groups;
    /**
     * 新分组序号 -> (新分组值, 旧分组序号)
     */
    private Map<Integer, Pair<Derive, IntList>> map = new HashMap<Integer, Pair<Derive, IntList>>();
    /**
     * 旧值序号 -> 新值序号
     */
    private int[] reverseMap;

    BaseCustomGroupRule(List<CustomGroup<Base, Derive>> groups, Derive otherGroupName) {
        this.otherGroupName = otherGroupName;
        this.groups = groups;
    }

    /**
     * 初始化映射关系
     */
    private void initMap() {
        int lastIndex = groups.size() + 1;

        int dictSize = dictColumn.size();
        reverseMap = new int[dictSize];

        // 0号为null
        map.put(0, Pair.of((Derive) null, IntListFactory.newSingleList(0)));

        for (int i = 1; i < dictSize; i++) {
            Base val = dictColumn.getValue(i);
            int index = findIndexByValue(val);

            Derive groupName;
            if (index != -1) {
                // 在区间里
                groupName = groups.get(index - 1).getName();
            } else {
                if (hasOtherGroup()) {
                    // 有其他组，则全部分到其他
                    index = lastIndex;
                    groupName = otherGroupName;
                } else {
                    // 不在区间里，又没有其他分组，则单独为一组
                    index = lastIndex++;
                    groupName = format(val);
                }
            }

            internalMap(i, index, groupName);
        }

        compactMap(lastIndex);
    }

    private int findIndexByValue(Base val) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).contains(val)) {
                // 有效字典序号从1开始
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public void setOriginDict(DictionaryEncodedColumn<Base> dict) {
        this.dictColumn = dict;
        initMap();
    }

    @Override
    public Derive getValue(int index) {
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

    /**
     * 有其他组
     *
     * @return 是否
     */
    abstract boolean hasOtherGroup();

    private void internalMap(int oldIndex, int newIndex, Derive groupName) {
        if (map.containsKey(newIndex)) {
            map.get(newIndex).getValue().add(oldIndex);
        } else {
            IntList indices = IntListFactory.createIntList();
            indices.add(oldIndex);
            map.put(newIndex, Pair.of(groupName, indices));
        }

        reverseMap[oldIndex] = newIndex;
    }

    private void compactMap(int oldSize) {
        // 压缩map，没分到值的分组要去掉
        for (int i = 1, j = 1; i < oldSize; i++, j++) {
            while (!map.containsKey(i)) {
                i++;
            }
            if (i == j) {
                continue;
            }
            map.put(j, map.get(i));
            updateReverseMap(i, j);
            if (i > j) {
                map.remove(i);
            }
        }
    }

    private void updateReverseMap(int oldIndex, int newIndex) {
        if (oldIndex == newIndex) {
            return;
        }
        for (int i = 0; i < reverseMap.length; i++) {
            if (reverseMap[i] == oldIndex) {
                reverseMap[i] = newIndex;
            }
        }
    }

    /**
     * 格式化
     *
     * @param val 原值
     * @return 新值
     */
    abstract Derive format(Base val);

    public abstract static class CustomGroup<Base, Derive> implements CoreService {
        @CoreField
        Derive name;

        Derive getName() {
            return name;
        }

        abstract List<Base> values();

        boolean contains(Base val) {
            return values().contains(val);
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