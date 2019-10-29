package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.CustomGroupRule;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.source.core.CoreService;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/3/2
 */
abstract class BaseCustomGroupRule<Base, Derive> extends BaseGroupRule implements CustomGroupRule<Base, Derive> {
    DictionaryEncodedColumn<Base> dictColumn;

    @CoreField
    Derive otherGroupName;

    @CoreField
    List<? extends CustomGroup<Base, Derive>> groups;
    /**
     * 新分组序号 -> (新分组值, 旧分组序号)
     */
    private Map<Integer, Pair<Derive, IntList>> map = new HashMap<Integer, Pair<Derive, IntList>>();

    /**
     * 旧值序号 -> 新值序号集合
     */
    private Map<Integer, IntList> reverseMap = new HashMap<Integer, IntList>();

    BaseCustomGroupRule(List<? extends CustomGroup<Base, Derive>> groups, Derive otherGroupName) {
        this.otherGroupName = otherGroupName;
        this.groups = groups;
    }

    /**
     * 初始化映射关系
     */
    private void initMap() {
        int lastIndex = groups.size() + 1;

        int dictSize = dictColumn.size();

        // 0号为null
        map.put(0, Pair.of((Derive) null, IntListFactory.newSingletonList(0)));

        for (int i = 1; i < dictSize; i++) {
            Base val = dictColumn.getValue(i);
            IntList indices = findIndexByValue(val);

            List<Derive> groupNames = new ArrayList<Derive>();
            if (indices.size() > 0) {
                // 在区间里
                for (int j = 0; j < indices.size(); j++) {
                    groupNames.add(groups.get(indices.get(j) - 1).getName());
                }
            } else {
                if (hasOtherGroup()) {
                    // 有其他组，则全部分到其他
                    indices.add(lastIndex);
                    groupNames.add(otherGroupName);
                } else {
                    // 不在区间里，又没有其他分组，则单独为一组
                    indices.add(lastIndex++);
                    groupNames.add(format(val));
                }
            }

            internalMap(i, indices, groupNames);
        }

        toFinalMappings();
    }

    private void toFinalMappings() {
        List<Entry<Integer, Pair<Derive, IntList>>> mappings = new ArrayList<Entry<Integer, Pair<Derive, IntList>>>(map.entrySet());
        Collections.sort(mappings, getComparator());

        map.clear();

        for (int i = 0; i < mappings.size(); i++) {
            map.put(i, mappings.get(i).getValue());
        }

        for (Entry<Integer, Pair<Derive, IntList>> entry : map.entrySet()) {
            IntList oldIndices = entry.getValue().getValue();
            for (int i = 0; i < oldIndices.size(); i++) {
                int oldIndex = oldIndices.get(i);
                if (!reverseMap.containsKey(oldIndex)) {
                    reverseMap.put(oldIndex, IntListFactory.createIntList(1));
                }
                reverseMap.get(oldIndex).add(entry.getKey());
            }
        }
    }

    Comparator<Entry<Integer, Pair<Derive, IntList>>> getComparator() {
        return new Comparator<Entry<Integer, Pair<Derive, IntList>>>() {
            @Override
            public int compare(Entry<Integer, Pair<Derive, IntList>> o1, Entry<Integer, Pair<Derive, IntList>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        };
    }

    private IntList findIndexByValue(Base val) {
        IntList ints = IntListFactory.createHeapIntList();
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).contains(val)) {
                // 有效字典序号从1开始
                ints.add(i + 1);
            }
        }
        return ints;
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
            if (Util.equals(getValue(i), val)) {
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
    public IntList reverseMap(int originIndex) {
        return reverseMap.get(originIndex);
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

    private void internalMap(int oldIndex, IntList newIndices, List<Derive> groupNames) {
        for (int i = 0; i < newIndices.size(); i++) {
            if (map.containsKey(newIndices.get(i))) {
                map.get(newIndices.get(i)).getValue().add(oldIndex);
            } else {
                IntList indices = IntListFactory.createIntList();
                indices.add(oldIndex);
                map.put(newIndices.get(i), Pair.of(groupNames.get(i), indices));
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

    abstract static class CustomGroup<Base, Derive> implements CoreService {
        @CoreField
        Derive name;

        CustomGroup(Derive name) {
            this.name = name;
        }

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