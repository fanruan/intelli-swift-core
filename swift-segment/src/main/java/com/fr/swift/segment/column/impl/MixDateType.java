package com.fr.swift.segment.column.impl;


import java.util.Calendar;

/**
 * @author anchore
 * <p>
 * 可用于截取日期，如：
 * 截取年月：2017/11/30 12:23:34.456 => 2017/11/01 00:00:00.000
 * 截取年，季度（截取后取当季第一个月的第一天）：2017/11/30 12:23:34.456 => 2017/10/01 00:00:00.000
 */
public enum MixDateType {
    //
    Y_Q {
        @Override
        public long from(Calendar c) {
            int year = DateType.YEAR.from(c);
            int quarter = DateType.QUARTER.from(c);
            c.clear();
            DateType.YEAR.set(c, year);
            DateType.QUARTER.set(c, quarter);
            return c.getTimeInMillis();
        }
    },
    Y_M {
        @Override
        public long from(Calendar c) {
            int year = DateType.YEAR.from(c);
            int month = DateType.MONTH.from(c);
            c.clear();
            DateType.YEAR.set(c, year);
            DateType.MONTH.set(c, month);
            return c.getTimeInMillis();
        }
    },
    Y_D {
        @Override
        public long from(Calendar c) {
            int year = DateType.YEAR.from(c);
            int day = DateType.DAY.from(c);
            c.clear();
            DateType.YEAR.set(c, year);
            DateType.DAY.set(c, day);
            return c.getTimeInMillis();
        }
    },
    Y_W {
        @Override
        public long from(Calendar c) {
            int year = DateType.YEAR.from(c);
            int week = DateType.WEEK.from(c);

            c.add(Calendar.DAY_OF_WEEK, 1 - week);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            if (DateType.YEAR.from(c) != year) {
                // 一周跨两年，设为周一可能跑到去年了 重新改为本年第一天
                DateType.YEAR.set(c, year);
                DateType.MONTH.set(c, 1);
                DateType.DAY.set(c, 1);
            }
            return c.getTimeInMillis();
        }
    },
    M_D {
        @Override
        public long from(Calendar c) {
            int month = DateType.MONTH.from(c);
            int day = DateType.DAY.from(c);
            c.clear();
            DateType.MONTH.set(c, month);
            DateType.DAY.set(c, day);
            return c.getTimeInMillis();
        }
    },
    Y_M_D {
        @Override
        public long from(Calendar c) {
            int year = DateType.YEAR.from(c);
            int month = DateType.MONTH.from(c);
            int day = DateType.DAY.from(c);
            c.clear();
            DateType.YEAR.set(c, year);
            DateType.MONTH.set(c, month);
            DateType.DAY.set(c, day);
            return c.getTimeInMillis();
        }
    },
    Y_M_D_H {
        @Override
        public long from(Calendar c) {
            DateType.MINUTE.set(c, 0);
            DateType.SECOND.set(c, 0);
            DateType.MILLISECOND.set(c, 0);
            return c.getTimeInMillis();
        }
    },
    Y_M_D_H_M {
        @Override
        public long from(Calendar c) {
            DateType.SECOND.set(c, 0);
            DateType.MILLISECOND.set(c, 0);
            return c.getTimeInMillis();
        }
    },
    Y_M_D_H_M_S {
        @Override
        public long from(Calendar c) {
            DateType.MILLISECOND.set(c, 0);
            return c.getTimeInMillis();
        }
    };

    public abstract long from(Calendar c);
}