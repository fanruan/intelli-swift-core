package com.fr.swift.query.group.impl;

import com.fr.general.ComparatorUtils;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


public class BaseSortByOtherDimensionGroupRule<Base, Derive> extends BaseGroupRule {
    DictionaryEncodedColumn<Base> dictColumn;
//    @CoreField
//    List<SortByOtherDimensionGroup<Base, Derive>> groups;
    BitmapIndexedColumn otherBitmapIndexedColumn;
    /**
     * 新分组序号 -> (新分组值, 旧分组序号)
     */
    private Map<Integer, Pair<Base, Integer>> map = new HashMap<Integer, Pair<Base, Integer>>();
    /**
     * 旧值序号 -> 新值序号
     */
    private int[] reverseMap;
    /**
     * 新值序号 -> 旧值序号
     */
    private int[] reReverseMap;

    private SortType sortType;


    public BaseSortByOtherDimensionGroupRule(SortType sortType) {
        this.sortType = sortType;
    }
    /**
     * 初始化映射关系
     */
    private void initMap() {
        try {
            int dictSize = dictColumn.size();
            Comparator comparator;
            reverseMap = new int[dictSize];
            reReverseMap = new int[dictSize];

            // 0号为null
            map.put(0, Pair.of((Base) null, 0));
            if(sortType == SortType.ASC) {
                ASCMap(dictSize);
            }
            if(sortType == SortType.DESC) {
                DESCMap(dictSize);
            }

        } catch (Exception e) {

        }
    }

    private void ASCMap(int size) {
        for(int i = 1; i < size ;) {
            TreeSet<Base> set = new TreeSet<Base>(dictColumn.getComparator());
            otherBitmapIndexedColumn.getBitMapIndex(i).traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    int oldIndex = dictColumn.getIndexByRow(row);
                    set.add(dictColumn.getValue(oldIndex));
                }
            });
            internalMap(i, set);
            i += set.size();
        }
    }

    private void DESCMap(int size) {
        for(int i = size - 1; i > 0;) {
            TreeSet<Base> set = new TreeSet<Base>(dictColumn.getComparator());
            otherBitmapIndexedColumn.getBitMapIndex(i).traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    int oldIndex = dictColumn.getIndexByRow(row);
                    set.add(dictColumn.getValue(oldIndex));
                }
            });
            i -= set.size();
            internalMap(i + 1, set);

        }
    }

    private void internalMap (int index, TreeSet<Base> set) {
        for(Base value : set) {
            map.put(index, Pair.of(value, dictColumn.getIndex(value)));
            reverseMap[dictColumn.getIndex(value)] = index;
            reReverseMap[index] = dictColumn.getIndex(value);
            index ++;
        }
    }
    public void setOriginDictAndByBitMapColumn(DictionaryEncodedColumn<Base> dict, BitmapIndexedColumn bitmapIndexedColumn) {
        this.dictColumn = dict;
        this.otherBitmapIndexedColumn  = bitmapIndexedColumn;
        initMap();
    }

    public Base getValue(int index) {
        return map.get(index).getKey();
    }

    public int getIndex(Object val) {
        for(int i = 0; i < dictColumn.size(); i++) {
            if(ComparatorUtils.equals(getValue(i), val)) {
                return i;
            }
        }
        return -1;
    }

    public int reverseMap(int originIndex) {
        return reverseMap[originIndex];
    }

    public int reReverseMap(int newIndex) {
        return reReverseMap[newIndex];
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.OTHER_DIMENSION_SORT;
    }

}
