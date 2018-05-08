package com.fr.swift.segment.column.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.util.function.Function;

import java.util.Calendar;

/**
 * @author anchore
 * @date 2018/2/3
 * <p>
 * 日期分组用
 */
public class DateDerivers {
    public static Function<Long, Long> newDeriver(GroupType type) {
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
                return newSingleFieldDeriver(toDateType(type));
            default:
                return newTruncDeriver(toMixDateType(type));
        }
    }

    public static Function<Long, Long> newSingleFieldDeriver(final DateType dateType) {
        return new BaseDateDeriver() {
            @Override
            public Long apply(Long millis) {
                c.setTimeInMillis(millis);
                return (long) dateType.from(c);
            }
        };
    }

    public static Function<Long, Long> newTruncDeriver(final MixDateType type) {
        return new BaseDateDeriver() {
            @Override
            public Long apply(Long millis) {
                c.setTimeInMillis(millis);
                return type.from(c);
            }
        };
    }

    private static abstract class BaseDateDeriver implements Function<Long, Long> {
        Calendar c = Calendar.getInstance();
    }

    private static MixDateType toMixDateType(GroupType type) {
        switch (type) {
            case Y_Q:
                return MixDateType.Y_Q;
            case M_D:
                return MixDateType.M_D;
            case Y_W:
                return MixDateType.Y_W;
            case Y_D:
                return MixDateType.Y_D;
            case Y_M:
                return MixDateType.Y_M;
            case Y_M_D:
                return MixDateType.Y_M_D;
            case Y_M_D_H:
                return MixDateType.Y_M_D_H;
            case Y_M_D_H_M:
                return MixDateType.Y_M_D_H_M;
            case Y_M_D_H_M_S:
                return MixDateType.Y_M_D_H_M_S;
            default:
                return null;
        }
    }

    private static DateType toDateType(GroupType type) {
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