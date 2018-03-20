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

    DateType() {
        this(UNDEF);
    }

    DateType(int type) {
        this.type = type;
    }

    public int from(Calendar c) {
        return c.get(type);
    }

    public void set(Calendar c, int value) {
        c.set(type, value);
    }

    enum MixDateType {
        Y_Q {
            @Override
            long from(Calendar c) {
                int year = YEAR.from(c);
                int quarter = QUARTER.from(c);
                c.clear();
                YEAR.set(c, year);
                QUARTER.set(c, quarter);
                return c.getTimeInMillis();
            }
        },
        Y_M {
            @Override
            long from(Calendar c) {
                int year = YEAR.from(c);
                int month = MONTH.from(c);
                c.clear();
                YEAR.set(c, year);
                MONTH.set(c, month);
                return c.getTimeInMillis();
            }
        },
        Y_D {
            @Override
            long from(Calendar c) {
                int year = YEAR.from(c);
                int day = DAY.from(c);
                c.clear();
                YEAR.set(c, year);
                DAY.set(c, day);
                return c.getTimeInMillis();
            }
        },
        Y_W {
            @Override
            long from(Calendar c) {
                int year = YEAR.from(c);
                c.clear();
                YEAR.set(c, year);
                WEEK.set(c, Calendar.MONDAY);
                // 一周跨两年，设为周一可能跑到去年了 重新改为本年第一天
                if (YEAR.from(c) != year) {
                    YEAR.set(c, year);
                    MONTH.set(c, Calendar.JANUARY);
                    DAY.set(c, 1);
                }
                return c.getTimeInMillis();
            }
        },
        M_D {
            @Override
            long from(Calendar c) {
                int year = MONTH.from(c);
                int day = DAY.from(c);
                c.clear();
                MONTH.set(c, year);
                DAY.set(c, day);
                return c.getTimeInMillis();
            }
        },
        Y_M_D {
            @Override
            long from(Calendar c) {
                int year = YEAR.from(c);
                int month = MONTH.from(c);
                int day = DAY.from(c);
                c.clear();
                YEAR.set(c, year);
                MONTH.set(c, month);
                DAY.set(c, day);
                return c.getTimeInMillis();
            }
        },
        Y_M_D_H {
            @Override
            long from(Calendar c) {
                MINUTE.set(c, 0);
                SECOND.set(c, 0);
                MILLISECOND.set(c, 0);
                return c.getTimeInMillis();
            }
        },
        Y_M_D_H_M {
            @Override
            long from(Calendar c) {
                SECOND.set(c, 0);
                MILLISECOND.set(c, 0);
                return c.getTimeInMillis();
            }
        },
        Y_M_D_H_M_S {
            @Override
            long from(Calendar c) {
                MILLISECOND.set(c, 0);
                return c.getTimeInMillis();
            }
        };

        abstract long from(Calendar c);
    }
}