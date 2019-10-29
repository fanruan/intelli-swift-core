package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.structure.array.IntList;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/28
 */
public class CustomNumGroupRule extends BaseCustomStrGroupRule<Number> {
    static final Format FORMATTER = new DecimalFormat("#.##");

    public CustomNumGroupRule(List<? extends CustomGroup<Number, String>> groups, String otherGroupName, boolean sorted) {
        super(groups, otherGroupName, sorted);
    }

    @Override
    String format(Number val) {
        return FORMATTER.format(val);
    }

    private int findIndexByName(String name) {
        for (int i = 0, size = groups.size(); i < size; i++) {
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
        //
        if (hasOtherGroup()) {
            return groups.size() + 1;
        }

        IntList indices = map(index);
        assert indices.size() == 1;
        Number val = dictColumn.getValue(indices.get(0));

        // 算全局字典偏移
        int offset = 0;
        offset += groups.size();

        return dictColumn.getGlobalIndexByIndex(dictColumn.getIndex(val)) + offset;
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CUSTOM_NUMBER;
    }

    public static class NumIntervals extends CustomGroup<Number, String> {
        @CoreField
        List<NumInterval> intervals = new ArrayList<NumInterval>(1);

        public NumIntervals(String name, List<NumInterval> intervals) {
            super(name);
            this.intervals = intervals;
        }

        public NumIntervals(String name) {
            super(name);
        }

        @Override
        public boolean contains(Number value) {
            for (NumInterval interval : intervals) {
                if (interval.contains(value)) {
                    return true;
                }
            }
            return false;
        }

        public void addInterval(NumInterval interval) {
            intervals.add(interval);
        }

        @Override
        List<Number> values() {
            return null;
        }
    }

    public static class NumInterval {
        /**
         * 是否为小于等于
         */
        @CoreField
        boolean lessOrEq;
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
         * 上界
         */
        @CoreField
        private double ceil;

        public NumInterval(double floor, boolean greaterOrEq, double ceil, boolean lessOrEq) {
            this.greaterOrEq = greaterOrEq;
            this.floor = floor;
            this.lessOrEq = lessOrEq;
            this.ceil = ceil;
        }

        public boolean contains(Number value) {
            double val = value.doubleValue();
            if (Double.compare(val, floor) == 0 && greaterOrEq) {
                return true;
            }
            if (val > floor && val < ceil) {
                return true;
            }
            return Double.compare(val, ceil) == 0 && lessOrEq;
        }
    }
}