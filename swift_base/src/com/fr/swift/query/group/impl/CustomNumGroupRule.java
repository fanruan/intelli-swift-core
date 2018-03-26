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
import java.text.NumberFormat;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/28
 */
public class CustomNumGroupRule extends BaseCustomGroupRule<Number> {
    static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#.##");
    @CoreField
    private List<NumInterval> intervals;

    public CustomNumGroupRule(List<NumInterval> intervals, String otherGroupName) {
        super(otherGroupName);
        this.intervals = intervals;
    }

    @Override
    void initMap() {
        int lastIndex = intervals.size();

        int dictSize = dictColumn.size();
        reverseMap = new int[dictSize];

        for (int i = 1; i < dictSize; i++) {
            Number num = dictColumn.getValue(i);
            int index = findIndex(num);

            String groupName;
            if (index != -1) {
                // 在区间里
                groupName = intervals.get(index).name;
            } else {
                if (hasOtherGroup()) {
                    // 有其他组，则全部分到其他
                    index = lastIndex;
                    groupName = otherGroupName;
                } else {
                    // 不在区间里，又没有其他分组，则单独为一组
                    index = lastIndex++;
                    groupName = NUMBER_FORMAT.format(num);
                }
            }

            if (map.containsKey(index)) {
                map.get(index).getValue().add(i);
            } else {
                IntList indices = IntListFactory.createIntList();
                indices.add(i);
                map.put(index, Pair.of(groupName, indices));
                reverseMap[i] = index;
            }
        }
    }

    private int findIndex(Number num) {
        for (int i = 0, size = intervals.size(); i < size; i++) {
            if (intervals.get(i).contains(num.doubleValue())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CUSTOM_NUMBER;
    }

    public static class NumInterval implements CoreService{
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

        private boolean contains(double val) {
            if (Double.compare(val, floor) == 0 && greaterOrEq) {
                return true;
            }
            if (val > floor && val < ceil) {
                return true;
            }
            return Double.compare(val, ceil) == 0 && lessOrEq;
        }

        public Core fetchObjectCore() {
            try {
                return new CoreGenerator(this).fetchObjectCore();
            } catch(Exception ignore) {

            }
            return Core.EMPTY_CORE;
        }
    }
}