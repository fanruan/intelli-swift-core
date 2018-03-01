package com.fr.swift.query.group.impl;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/29
 */
public class AutoGroupRule extends CustomNumGroupRule {
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#.##");

    public AutoGroupRule(DictionaryEncodedColumn<? extends Number> dictColumn, Partition partition) {
        super(dictColumn, toNumIntervals(partition));
    }

    private static List<NumInterval> toNumIntervals(Partition p) {
        List<NumInterval> intervals = new ArrayList<NumInterval>();

        double floor;
        for (int i = 0; (floor = border(i, p)) < p.max; i++) {
            double ceil = border(i + 1, p);
            intervals.add(new NumInterval(getIntervalName(floor, ceil),
                    floor, true, ceil, false));
        }
        // 最右区间边界为<=
        intervals.get(intervals.size() - 1).lessOrEq = true;
        return intervals;
    }

    private static double border(int index, Partition p) {
        return p.min + index * p.interval;
    }

    private static String getIntervalName(double floor, double ceil) {
        return NUMBER_FORMAT.format(floor) + " - " + NUMBER_FORMAT.format(ceil);
    }

    public static class Partition {
        double min, max, interval;

        public Partition(double min, double max, double interval) {
            this.min = min;
            this.max = max;
            this.interval = interval;
        }
    }
}