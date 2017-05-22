package com.fr.bi.base;

import com.fr.bi.stable.constant.BIReportConstant;
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

        @Override
        public Integer result2Value(Long t, Calendar calendar) {
            return BITimeUtils.getFieldFromTime(t, Calendar.YEAR, calendar);
        }
    };
    private static final ValueConverter<Long, Integer> MONTH = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.MONTH) + 1;
        }

        @Override
        public Integer result2Value(Long t, Calendar calendar) {
            return BITimeUtils.getFieldFromTime(t, Calendar.MONTH, calendar) + 1;
        }
    };
    private static final ValueConverter<Long, Integer> SEASON = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            int month = BITimeUtils.getFieldFromTime(t, Calendar.MONTH);
            return getSeason(month);
        }

        @Override
        public Integer result2Value(Long t, Calendar calendar) {
            int month = BITimeUtils.getFieldFromTime(t, Calendar.MONTH, calendar);
            return getSeason(month);
        }
    };
    private static final ValueConverter<Long, Integer> HOUR = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.HOUR_OF_DAY);
        }

        @Override
        public Integer result2Value(Long t, Calendar calendar) {
            return BITimeUtils.getFieldFromTime(t, Calendar.HOUR_OF_DAY, calendar);
        }
    };
    private static final ValueConverter<Long, Integer> MINUTE = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.MINUTE);
        }

        @Override
        public Integer result2Value(Long t, Calendar calendar) {
            return BITimeUtils.getFieldFromTime(t, Calendar.MINUTE, calendar);
        }
    };
    private static final ValueConverter<Long, Integer> SECOND = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.SECOND);
        }

        @Override
        public Integer result2Value(Long t, Calendar calendar) {
            return BITimeUtils.getFieldFromTime(t, Calendar.SECOND, calendar);
        }
    };
    private static final ValueConverter<Long, Integer> MONTH_DAY = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            return BITimeUtils.getFieldFromTime(t, Calendar.DAY_OF_MONTH);
        }

        @Override
        public Integer result2Value(Long t, Calendar calendar) {
            return BITimeUtils.getFieldFromTime(t, Calendar.DAY_OF_MONTH, calendar);
        }
    };
    private static final ValueConverter<Long, Integer> WEEK = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            int week = BITimeUtils.getFieldFromTime(t, Calendar.DAY_OF_WEEK) - 1;
            return week == 0 ? DateConstant.CALENDAR.WEEK.SUNDAY_7: week;
        }

        @Override
        public Integer result2Value(Long t, Calendar calendar) {
            int week = BITimeUtils.getFieldFromTime(t, Calendar.DAY_OF_WEEK, calendar) - 1;
            return week == 0 ? DateConstant.CALENDAR.WEEK.SUNDAY_7: week;
        }
    };
    private static final ValueConverter<Long, Long> YMD = new ValueConverter<Long, Long>() {

        @Override
        public Long result2Value(Long t) {
            return BIDateUtils.toSimpleDay(t);
        }

        @Override
        public Long result2Value(Long t, Calendar calendar) {
            return BIDateUtils.toSimpleDay(t, calendar);
        }
    };
    private static final ValueConverter<Long, Long> YEAR_MONTH_DAY_HOUR = new ValueConverter<Long, Long>() {

        @Override
        public Long result2Value(Long t) {
            return BIDateUtils.toYearMonthDayHour(t);
        }

        @Override
        public Long result2Value(Long t, Calendar calendar) {
            return BIDateUtils.toYearMonthDayHour(t, calendar);
        }
    };
    private static final ValueConverter<Long, Long> YEAR_MONTH_DAY_HOUR_MINUTE = new ValueConverter<Long, Long>() {

        @Override
        public Long result2Value(Long t) {
            return BIDateUtils.toYearMonthDayHourMinute(t);
        }

        @Override
        public Long result2Value(Long t, Calendar calendar) {
            return BIDateUtils.toYearMonthDayHourMinute(t, calendar);
        }
    };
    private static final ValueConverter<Long, Long> YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = new ValueConverter<Long, Long>() {

        @Override
        public Long result2Value(Long t) {
            return BIDateUtils.toYearMonthDayHourMinuteSecond(t);
        }

        @Override
        public Long result2Value(Long t, Calendar c) {
            return BIDateUtils.toYearMonthDayHourMinuteSecond(t, c);
        }
    };
    private static final ValueConverter<Long, String> YEAR_MONTH = new ValueConverter<Long, String>() {

        @Override
        public String result2Value(Long t) {
            Integer month = BITimeUtils.getFieldFromTime(t, Calendar.MONTH) + 1;
            Integer year = BITimeUtils.getFieldFromTime(t, Calendar.YEAR);
            return year+"-"+month;
        }
    };
    private static final ValueConverter<Long, String> YEAR_WEEKNUMBER = new ValueConverter<Long, String>() {

        @Override
        public String result2Value(Long t) {
            Integer weekNumber = BITimeUtils.getFieldFromTime(t, Calendar.WEEK_OF_YEAR);
            Integer year = BITimeUtils.getFieldFromTime(t, Calendar.YEAR);
            return year+"-"+weekNumber;
        }
    };
    private static final ValueConverter<Long, Integer> WEEKNUMBER = new ValueConverter<Long, Integer>() {

        @Override
        public Integer result2Value(Long t) {
            Integer weekNumber = BITimeUtils.getFieldFromTime(t, Calendar.WEEK_OF_YEAR);
            return weekNumber;
        }
    };
    private static final ValueConverter<Long, String> YEAR_SEASON = new ValueConverter<Long, String>() {

        @Override
        public String result2Value(Long t) {
            int month = BITimeUtils.getFieldFromTime(t, Calendar.MONTH);
            Integer year = BITimeUtils.getFieldFromTime(t, Calendar.YEAR);
            return year+"-"+getSeason(month);
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
                return MONTH_DAY;
            }
            case DateConstant.DATE.WEEK: {
                return WEEK;
            }
            case DateConstant.DATE.YMD: {
                return YMD;
            }
            case DateConstant.DATE.YEAR_MONTH: {
                return YEAR_MONTH;
            }
            case DateConstant.DATE.YEAR_SEASON: {
                return YEAR_SEASON;
            }
            case DateConstant.DATE.YEAR_WEEKNUMBER: {
                return YEAR_WEEKNUMBER;
            }
            case DateConstant.DATE.WEEKNUMBER: {
                return WEEKNUMBER;
            }
            case DateConstant.DATE.HOUR:{
                return HOUR;
            }
            case DateConstant.DATE.MINUTE:{
                return MINUTE;
            }
            case DateConstant.DATE.SECOND:{
                return SECOND;
            }
            case DateConstant.DATE.YEAR_MONTH_DAY_HOUR: {
                return YEAR_MONTH_DAY_HOUR;
            }
            case DateConstant.DATE.YEAR_MONTH_DAY_HOUR_MINUTE: {
                return YEAR_MONTH_DAY_HOUR_MINUTE;
            }
            case DateConstant.DATE.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND: {
                return YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;
            }
            default: {
                return ValueConverter.DEFAULT;
            }
        }
    }

    public static ValueConverter createDateValueConverterByGroupType(int type) {
        switch (type) {
            case BIReportConstant.GROUP.Y: {
                return YEAR;
            }
            case BIReportConstant.GROUP.M: {
                return MONTH;
            }
            case  BIReportConstant.GROUP.S: {
                return SEASON;
            }
            case  BIReportConstant.GROUP.MD: {
                return MONTH_DAY;
            }
            case  BIReportConstant.GROUP.W: {
                return WEEK;
            }
            case  BIReportConstant.GROUP.YMD: {
                return YMD;
            }
            default: {
                return ValueConverter.DEFAULT;
            }
        }
    }


}