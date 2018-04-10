package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.source.core.CoreService;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/28
 */
public class CustomNumGroupRule extends BaseCustomGroupRule<Number> {
    static final Format FORMATTER = new DecimalFormat("#.##");

    @CoreField
    private List<NumInterval> intervals;

    public CustomNumGroupRule(List<NumInterval> intervals, String otherGroupName) {
        super(otherGroupName);
        this.intervals = intervals;
    }

    @Override
    void initMap() {
        int lastIndex = intervals.size() + 1;

        int dictSize = dictColumn.size();
        reverseMap = new int[dictSize];

        // 0号为null
        map.put(0, Pair.of((String) null, IntListFactory.newSingleList(0)));

        for (int i = 1; i < dictSize; i++) {
            Number num = dictColumn.getValue(i);
            int index = findIndexByValue(num);

            String groupName;
            if (index != -1) {
                // 在区间里
                groupName = intervals.get(index - 1).name;
            } else {
                if (hasOtherGroup()) {
                    // 有其他组，则全部分到其他
                    index = lastIndex;
                    groupName = otherGroupName;
                } else {
                    // 不在区间里，又没有其他分组，则单独为一组
                    index = lastIndex++;
                    groupName = FORMATTER.format(num);
                }
            }

            internalMap(i, index, groupName);
        }

        compactMap(lastIndex);
    }

    private int findIndexByValue(Number num) {
        for (int i = 0, size = intervals.size(); i < size; i++) {
            if (intervals.get(i).contains(num)) {
                // 有效字典序号从1开始
                return i + 1;
            }
        }
        return -1;
    }

    private int findIndexByName(String name) {
        for (int i = 0, size = intervals.size(); i < size; i++) {
            if (intervals.get(i).name.equals(name)) {
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
        //
        if (hasOtherGroup()) {
            return intervals.size() + 1;
        }

        IntList indices = map(index);
        assert indices.size() == 1;
        Number val = dictColumn.getValue(indices.get(0));

        // 算全局字典偏移
        int offset = 0;
        offset += intervals.size();

        return dictColumn.getGlobalIndexByIndex(dictColumn.getIndex(val)) + offset;
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CUSTOM_NUMBER;
    }

    public static class NumInterval implements CoreService {
        /**
         * 是否为大于等于
         */
        @CoreField
        private boolean greaterOrEq;
        /**
         * 下界
         */
        @CoreField
        private double floor;
        /**
         * 是否为小于等于
         */
        @CoreField
        boolean lessOrEq;
        /**
         * 上界
         */
        @CoreField
        private double ceil;

        @CoreField
        private String name;

        public NumInterval(String name, double floor, boolean greaterOrEq, double ceil, boolean lessOrEq) {
            this.greaterOrEq = greaterOrEq;
            this.floor = floor;
            this.lessOrEq = lessOrEq;
            this.ceil = ceil;
            this.name = name;
        }

        private boolean contains(Number value) {
            double val = value.doubleValue();
            if (Double.compare(val, floor) == 0 && greaterOrEq) {
                return true;
            }
            if (val > floor && val < ceil) {
                return true;
            }
            return Double.compare(val, ceil) == 0 && lessOrEq;
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