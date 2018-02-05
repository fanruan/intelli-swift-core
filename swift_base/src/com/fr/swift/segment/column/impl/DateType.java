package com.fr.swift.segment.column.impl;

import java.util.Calendar;

/**
 * @author anchore
 * @date 2017/11/29
 */
public enum DateType {
    /**
     * 日期类型
     */
    YEAR(Calendar.YEAR),
    MONTH(Calendar.MONTH),
    WEEK(Calendar.DAY_OF_WEEK),
    DAY(Calendar.DAY_OF_MONTH),
    HOUR(Calendar.HOUR_OF_DAY),
    MINUTE(Calendar.MINUTE),
    SECOND(Calendar.SECOND),
    MILLISECOND(Calendar.MILLISECOND),

    // 季度
    QUARTER {
        @Override
        public int get(Calendar c) {
            int month = MONTH.get(c);
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

    DateType() {
        this(UNDEF);
    }

    DateType(int type) {
        this.type = type;
    }

    public int get(Calendar c) {
        return c.get(type);
    }

    public void set(Calendar c, int value) {
        c.set(type, value);
    }
}