package com.fr.swift.segment.column.impl;

import java.util.Calendar;

/**
 * @author anchore
 * @date 2017/11/29
 * <p>
 * 可用于提取日期子字段，如：
 * 年、月、日、时、分、秒、毫秒
 * 季度、全年第几周等
 */
public enum DateType {
    /**
     * 日期类型
     */
    YEAR(Calendar.YEAR, 1),

    /**
     * 1-12 -> Jan-Dec
     */
    MONTH(Calendar.MONTH, 12) {
        @Override
        public int from(Calendar c) {
            return super.from(c) + 1;
        }

        @Override
        public void set(Calendar c, int value) {
            super.set(c, value - 1);
        }
    },

    /**
     * 1-7 -> Mon-Sun
     */
    WEEK(Calendar.DAY_OF_WEEK) {
        @Override
        public int from(Calendar c) {
            int week = super.from(c);
            if (Calendar.SUNDAY == week) {
                return 7;
            }
            return week - 1;
        }

        @Override
        public void set(Calendar c, int value) {
            if (value == 7) {
                value = Calendar.SUNDAY;
            } else {
                value++;
            }
            super.set(c, value);
        }
    },

    DAY(Calendar.DAY_OF_MONTH),

    HOUR(Calendar.HOUR_OF_DAY, 24),

    MINUTE(Calendar.MINUTE, 60),

    SECOND(Calendar.SECOND, 60),

    MILLISECOND(Calendar.MILLISECOND, 1000),

    // 季度
    QUARTER(-1, 4) {
        @Override
        public int from(Calendar c) {
            int month = MONTH.from(c);
            return (month - 1) / 3 + 1;
        }

        /**
         * 设置为该季度第一个月的第一天
         */
        @Override
        public void set(Calendar c, int value) {
            int month = (value - 1) * 3 + 1;
            MONTH.set(c, month);
            DAY.set(c, 1);
        }
    },

    WEEK_OF_YEAR(Calendar.WEEK_OF_YEAR) {
        @Override
        public int from(Calendar c) {
            long t = c.getTimeInMillis();
            int weekOfYear = super.from(c);
            int year = c.get(Calendar.YEAR);

            // 回到上周，如果还在本年，但是周数变大了，说明本周算到下一年了
            c.add(Calendar.DAY_OF_MONTH, -7);
            int lastWeekOfYear = c.get(Calendar.WEEK_OF_YEAR);
            if (year == c.get(Calendar.YEAR) && weekOfYear < lastWeekOfYear) {
                return lastWeekOfYear + 1;
            }

            c.setFirstDayOfWeek(Calendar.MONDAY);
            c.setMinimalDaysInFirstWeek(1);
            c.setTimeInMillis(t);
            return c.get(Calendar.WEEK_OF_YEAR);
        }
    };

    private static final int UNDEF = -1;
    private final int type;
    public final int radix;

    DateType(int type) {
        this(type, UNDEF);
    }

    DateType(int type, int radix) {
        this.type = type;
        this.radix = radix;
    }

    public int from(Calendar c) {
        return c.get(type);
    }

    public void set(Calendar c, int value) {
        c.set(type, value);
    }
}