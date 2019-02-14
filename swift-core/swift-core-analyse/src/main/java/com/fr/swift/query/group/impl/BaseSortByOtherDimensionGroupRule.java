package com.fr.swift.query.group.impl;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


public class BaseSortByOtherDimensionGroupRule<Base, Derive> extends BaseGroupRule {
    DictionaryEncodedColumn<Base> dictColumn;
    Column<Base> sortByColumn;
    //    @CoreField
//    List<SortByOtherDimensionGroup<Base, Derive>> groups;
    BitmapIndexedColumn sortByBitmapIndexedColumn;
    /**
     * 新分组序号 -> (新分组值, 旧分组序号)
     */
    private Map<Integer, Pair<Base, Integer>> map = new HashMap<Integer, Pair<Base, Integer>>();
    /**
     * 旧值序号 -> 新值序号
     */
    private int[] reverseMap;

    private SortType sortType;

    private int[] indexArray;

    public BaseSortByOtherDimensionGroupRule(SortType sortType) {
        this.sortType = sortType;
    }

    /**
     * 初始化映射关系
     */
    private void initMap() {
        try {
            int dictSize = dictColumn.size();
            reverseMap = new int[dictSize];
            indexArray = new int[dictSize];
            // 0号为null
            map.put(0, Pair.of((Base) null, 0));
            if (sortType == SortType.ASC) {
                ASCMap(dictSize);
            }
            if (sortType == SortType.DESC) {
                DESCMap(dictSize);
            }

        } catch (Exception e) {

        }
    }

    private void ASCMap(int size) {
        for (int i = 0, j = 1; j < size; i++) {
            final TreeSet<Base> set = new TreeSet<Base>(dictColumn.getComparator());
            sortByBitmapIndexedColumn.getBitMapIndex(i).traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    int oldIndex = dictColumn.getIndexByRow(row);
                    if (!findIndex(oldIndex)) {
                        if (dictColumn.getValue(oldIndex) != null) {
                            set.add(dictColumn.getValue(oldIndex));
                        }
                        indexArray[oldIndex] = 1;
                    }
                }
            });
            internalMap(j, set);
            j += set.size();
        }
    }

    private void DESCMap(int size) {
        for (int i = sortByColumn.getDictionaryEncodedColumn().size() - 1, j = size; j > 1; i--) {
            final TreeSet<Base> set = new TreeSet<Base>(dictColumn.getComparator());
            sortByBitmapIndexedColumn.getBitMapIndex(i).traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    int oldIndex = dictColumn.getIndexByRow(row);
                    if (!findIndex(oldIndex)) {
                        indexArray[oldIndex] = 1;
                        if (dictColumn.getValue(oldIndex) != null) {
                            set.add(dictColumn.getValue(oldIndex));
                        }
                    }
                }
            });

            j -= set.size();
            internalMap(j, set);
        }
    }

    private boolean findIndex(int oldIndex) {
        if (indexArray[oldIndex] == 1) {
            return true;
        }
        return false;
    }

    private void internalMap(int index, TreeSet<Base> set) {
        for (Base value : set) {
            map.put(index, Pair.of(value, dictColumn.getIndex(value)));
            reverseMap[dictColumn.getIndex(value)] = index;
            index++;
        }
    }

    public void setOriginDictAndByBitMapColumn(DictionaryEncodedColumn<Base> dict, Column column) {
        this.dictColumn = dict;
        this.sortByColumn = column;
        this.sortByBitmapIndexedColumn = column.getBitmapIndex();
        initMap();
    }

    public Base getValue(int index) {
        return map.get(index).getKey();
    }

    public int getIndex(Object val) {
        for (int i = 0; i < dictColumn.size(); i++) {
            if (Util.equals(getValue(i), val)) {
                return i;
            }
        }
        return -1;
    }

    public int reverseMap(int originIndex) {
        return reverseMap[originIndex];
    }

    public int map(int newIndex) {
        return map.get(newIndex).getValue();
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.OTHER_DIMENSION_SORT;
    }

}
