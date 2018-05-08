package com.fr.swift.query.group.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.structure.array.IntList;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/28
 */
public class CustomNumGroupRule extends BaseCustomGroupRule<Number, String> {
    static final Format FORMATTER = new DecimalFormat("#.##");

    public CustomNumGroupRule(List<? extends CustomGroup<Number, String>> groups, String otherGroupName) {
        super(groups, otherGroupName);
    }

    @Override
    boolean hasOtherGroup() {
        return StringUtils.isNotEmpty(otherGroupName);
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

    public static class NumInterval extends CustomGroup<Number, String> {
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

        public NumInterval(String name, double floor, boolean greaterOrEq, double ceil, boolean lessOrEq) {
            super(name);
            this.greaterOrEq = greaterOrEq;
            this.floor = floor;
            this.lessOrEq = lessOrEq;
            this.ceil = ceil;
        }

        @Override
        public List<Number> values() {
            return null;
        }

        @Override
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