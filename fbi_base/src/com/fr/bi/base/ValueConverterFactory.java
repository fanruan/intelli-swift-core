package com.fr.bi.base;

import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.bi.stable.utils.time.BITimeUtils;

import java.util.Calendar;

public class ValueConverterFactory {

    private static final ValueConverter<Long, Integer> YEAR = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.YEAR);
        }
    };


    private static final ValueConverter<Long, Integer> MONTH = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.MONTH);
        }
    };
    private static final ValueConverter<Long, Integer> SEASON = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            int month = BITimeUtils.getFieldFromTime(t, Calendar.MONTH);
            return getSeason(month);
        }
    };
    private static final ValueConverter<Long, Integer> DAY = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.DAY_OF_MONTH);
        }
    };
    private static final ValueConverter<Long, Integer> WEEK = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.DAY_OF_WEEK);
        }
    };
    private static final ValueConverter<Long, Long> YMD = new ValueConverter<Long, Long>() {

        @Override
        public Long result2Value(Long t) {
            return BIDateUtils.toSimpleDay(t);
        }
    };

    private static int getSeason(int month) {
        return month / 3 + 1;
    }

    @SuppressWarnings("rawtypes")
    public static ValueConverter createDateValueConverter(int type) {
        switch (type) {
            case DateConstant.DATE.YEAR: {
                return YEAR;
            }
            case DateConstant.DATE.MONTH: {
                return MONTH;
            }
            case DateConstant.DATE.SEASON: {
                return SEASON;
            }
            case DateConstant.DATE.DAY: {
                return DAY;
            }
            case DateConstant.DATE.WEEK: {
                return WEEK;
            }
            case DateConstant.DATE.YMD: {
                return YMD;
            }
            default: {
                return ValueConverter.DEFAULT;
            }
        }
    }


}