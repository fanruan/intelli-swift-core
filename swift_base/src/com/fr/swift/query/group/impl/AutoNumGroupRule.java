package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/29
 * <p>
 * 数字自动分组规则
 */
public class AutoNumGroupRule extends CustomNumGroupRule {
    public AutoNumGroupRule(Partition partition) {
        // 无其他组，传null
        super(partition.toNumIntervals(), null);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.AUTO;
    }

    public static class Partition {
        private double min, max, interval;

        public Partition(double min, double max, double interval) {
            this.min = min;
            this.max = max;
            this.interval = interval;
        }

        private List<NumInterval> toNumIntervals() {
            List<NumInterval> intervals = new ArrayList<NumInterval>();

            double floor = min, ceil;
            for (int i = 1; floor < max; i++) {
                ceil = border(i);
                intervals.add(new NumInterval(getIntervalName(floor, ceil),
                        floor, true, ceil, false));
                floor = ceil;
            }
            // 最右区间边界为<=
            intervals.get(intervals.size() - 1).lessOrEq = true;
            return intervals;
        }

        private double border(int index) {
            double nextBorder = min + index * interval;
            // 最大不得超过max
            return nextBorder > max ? max : nextBorder;
        }

        private static String getIntervalName(double floor, double ceil) {
            return NUMBER_FORMAT.format(floor) + " - " + NUMBER_FORMAT.format(ceil);
        }
    }
}