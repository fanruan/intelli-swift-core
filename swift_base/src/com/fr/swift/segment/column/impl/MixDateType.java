package com.fr.swift.segment.column.impl;

import java.util.Calendar;

/**
 * @author anchore
 */
public enum MixDateType {
    Y_Q {
        @Override
        long from(Calendar c) {
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
        long from(Calendar c) {
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
        long from(Calendar c) {
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
        long from(Calendar c) {
            int year = DateType.YEAR.from(c);
            c.clear();
            DateType.YEAR.set(c, year);
            DateType.WEEK.set(c, Calendar.MONDAY);
            // 一周跨两年，设为周一可能跑到去年了 重新改为本年第一天
            if (DateType.YEAR.from(c) != year) {
                DateType.YEAR.set(c, year);
                DateType.MONTH.set(c, Calendar.JANUARY);
                DateType.DAY.set(c, 1);
            }
            return c.getTimeInMillis();
        }
    },
    M_D {
        @Override
        long from(Calendar c) {
            int year = DateType.MONTH.from(c);
            int day = DateType.DAY.from(c);
            c.clear();
            DateType.MONTH.set(c, year);
            DateType.DAY.set(c, day);
            return c.getTimeInMillis();
        }
    },
    Y_M_D {
        @Override
        long from(Calendar c) {
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
        long from(Calendar c) {
            DateType.MINUTE.set(c, 0);
            DateType.SECOND.set(c, 0);
            DateType.MILLISECOND.set(c, 0);
            return c.getTimeInMillis();
        }
    },
    Y_M_D_H_M {
        @Override
        long from(Calendar c) {
            DateType.SECOND.set(c, 0);
            DateType.MILLISECOND.set(c, 0);
            return c.getTimeInMillis();
        }
    },
    Y_M_D_H_M_S {
        @Override
        long from(Calendar c) {
            DateType.MILLISECOND.set(c, 0);
            return c.getTimeInMillis();
        }
    };

    abstract long from(Calendar c);
}