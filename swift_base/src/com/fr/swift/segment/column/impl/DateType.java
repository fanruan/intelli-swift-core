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

    MONTH(Calendar.MONTH, 12) {
        @Override
        public int from(Calendar c) {
            return super.from(c) + 1;
        }
    },

    WEEK(Calendar.DAY_OF_WEEK),

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
            return month / 3 + 1;
        }

        /**
         * 设置为该季度第一个月的第一天
         */
        @Override
        public void set(Calendar c, int value) {
            int month = (value - 1) * 3;
            MONTH.set(c, month);
            DAY.set(c, 1);
        }
    },

    WEEK_OF_YEAR(Calendar.WEEK_OF_YEAR);

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