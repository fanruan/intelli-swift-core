package com.fr.swift.segment.column.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.util.function.Function;

import java.util.Calendar;

/**
 * @author anchore
 * @date 2018/2/3
 */
class DateDerivers {
    @SuppressWarnings("unchecked")
    static <Base, Derive> Function<Base, Derive> newDeriver(GroupType type) {
        switch (type) {
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
            case WEEK_OF_YEAR:
                return (Function<Base, Derive>) newSingleFieldDeriver(from(type));
            default:
                return (Function<Base, Derive>) newTruncatedDeriver(type);
        }
    }

    private static Function<Long, Integer> newSingleFieldDeriver(final DateType dateType) {
        return new BaseDateDeriver<Integer>() {
            @Override
            public Integer apply(Long millis) {
                c.setTimeInMillis(millis);
                return dateType.get(c);
            }
        };
    }

    private static Function<Long, Long> newTruncatedDeriver(GroupType type) {
        switch (type) {
            case Y_Q:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        int year = DateType.YEAR.get(c);
                        int quarter = DateType.QUARTER.get(c);
                        c.clear();
                        DateType.YEAR.set(c, year);
                        DateType.QUARTER.set(c, quarter);
                        return c.getTimeInMillis();
                    }
                };
            case Y_M:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        int year = DateType.YEAR.get(c);
                        int month = DateType.MONTH.get(c);
                        c.clear();
                        DateType.YEAR.set(c, year);
                        DateType.MONTH.set(c, month);
                        return c.getTimeInMillis();
                    }
                };
            case Y_D:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        int year = DateType.YEAR.get(c);
                        int day = DateType.DAY.get(c);
                        c.clear();
                        DateType.YEAR.set(c, year);
                        DateType.DAY.set(c, day);
                        return c.getTimeInMillis();
                    }
                };
            case Y_W:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        int year = DateType.YEAR.get(c);
                        c.clear();
                        DateType.YEAR.set(c, year);
                        DateType.WEEK.set(c, Calendar.MONDAY);
                        // 一周跨两年，设为周一可能跑到去年了 重新改为本年第一天
                        if (DateType.YEAR.get(c) != year) {
                            DateType.YEAR.set(c, year);
                            DateType.MONTH.set(c, Calendar.JANUARY);
                            DateType.DAY.set(c, 1);
                        }
                        return c.getTimeInMillis();
                    }
                };
            case M_D:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        int year = DateType.MONTH.get(c);
                        int day = DateType.DAY.get(c);
                        c.clear();
                        DateType.MONTH.set(c, year);
                        DateType.DAY.set(c, day);
                        return c.getTimeInMillis();
                    }
                };
            case Y_M_D:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        int year = DateType.YEAR.get(c);
                        int month = DateType.MONTH.get(c);
                        int day = DateType.DAY.get(c);
                        c.clear();
                        DateType.YEAR.set(c, year);
                        DateType.MONTH.set(c, month);
                        DateType.DAY.set(c, day);
                        return c.getTimeInMillis();
                    }
                };
            case Y_M_D_H:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        DateType.MINUTE.set(c, 0);
                        DateType.SECOND.set(c, 0);
                        DateType.MILLISECOND.set(c, 0);
                        return c.getTimeInMillis();
                    }
                };
            case Y_M_D_H_M:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        DateType.SECOND.set(c, 0);
                        DateType.MILLISECOND.set(c, 0);
                        return c.getTimeInMillis();
                    }
                };
            case Y_M_D_H_M_S:
                return new BaseDateDeriver<Long>() {
                    @Override
                    public Long apply(Long millis) {
                        c.setTimeInMillis(millis);
                        DateType.MILLISECOND.set(c, 0);
                        return c.getTimeInMillis();
                    }
                };
            default:
                return null;
        }
    }

    private static abstract class BaseDateDeriver<Derive> implements Function<Long, Derive> {
        Calendar c = Calendar.getInstance();
    }

    private static DateType from(GroupType type) {
        switch (type) {
            case YEAR:
                return DateType.YEAR;
            case QUARTER:
                return DateType.QUARTER;
            case MONTH:
                return DateType.MONTH;
            case WEEK:
                return DateType.WEEK;
            case DAY:
                return DateType.DAY;
            case HOUR:
                return DateType.HOUR;
            case MINUTE:
                return DateType.MINUTE;
            case SECOND:
                return DateType.SECOND;
            case WEEK_OF_YEAR:
                return DateType.WEEK_OF_YEAR;
            default:
                return null;
        }
    }
}