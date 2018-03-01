package com.fr.swift.query.group.impl;

import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.util.Crasher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/2/28
 */
public class CustomNumGroupRule extends BaseGroupRule {
    private List<NumInterval> intervals;
    private boolean hasOtherGroup;

    private Map<Integer, IntList> map = new HashMap<Integer, IntList>();

    public CustomNumGroupRule(DictionaryEncodedColumn<? extends Number> dictColumn, List<NumInterval> intervals, boolean hasOtherGroup) {
        super(dictColumn);
        this.intervals = intervals;
        this.hasOtherGroup = hasOtherGroup;

        initMap();
    }

    @Override
    public String getGroupName(int index) {
        return intervals.get(index).name;
    }

    @Override
    public IntList map(int index) {
        return map.get(index);
    }

    private void initMap() {
        for (int i = 0; i < dictColumn.size(); i++) {
            Number num = (Number) dictColumn.getValue(i);
            int index = findIndex(num);

            if (map.containsKey(index)) {
                map.get(index).add(i);
            } else {
                IntList indices = IntListFactory.createIntList();
                indices.add(i);
                map.put(index, indices);
            }
        }
    }

    private int findIndex(Number num) {
        for (int i = 0, size = intervals.size(); i < size; i++) {
            NumInterval interval = intervals.get(i);
            double val = num.doubleValue();
            if (Double.compare(val, interval.floor) == 0 && interval.greaterOrEq) {
                return i;
            }
            if (val > interval.floor && val < interval.ceil) {
                return i;
            }
            if (Double.compare(val, interval.ceil) == 0 && interval.lessOrEq) {
                return i;
            }
        }
        if (hasOtherGroup) {
            // 未分组的分到最后一组
            return intervals.size();
        }
        return Crasher.crash("index not found");
    }

    @Override
    public int newSize() {
        // 如果未分组的分到其它，则size加1
        return map.size();
    }

    public static class NumInterval {
        /**
         * 是否为大于等于
         */
        boolean greaterOrEq;
        /**
         * 下界
         */
        double floor;
        /**
         * 是否为小于等于
         */
        boolean lessOrEq;
        /**
         * 上界
         */
        double ceil;

        String name;

        public NumInterval(String name, double floor, boolean greaterOrEq, double ceil, boolean lessOrEq) {
            this.greaterOrEq = greaterOrEq;
            this.floor = floor;
            this.lessOrEq = lessOrEq;
            this.ceil = ceil;
            this.name = name;
        }
    }
}