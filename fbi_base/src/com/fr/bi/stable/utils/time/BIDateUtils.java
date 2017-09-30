package com.fr.bi.stable.utils.time;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.general.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GUY on 2015/3/6.
 */
public class BIDateUtils {

    public static final Integer MAX_DAY = 31;

    public static final Integer MAX_MONTH = 11;

    public static final long MILLSIONOFWEEK = 7 * 86400000;

    /**
     * 生成cube开始生成时间
     *
     * @param hour 几点钟
     * @return Date日期
     */
    public static Date createStartDate(int hour, int frequency) {

        if (frequency == DBConstant.UPDATE_FREQUENCY.EVER_MONTH) {
            return createMonthStartDate(hour);
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        if (c.getTime().before(new Date())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        if (frequency != DBConstant.UPDATE_FREQUENCY.EVER_DAY) {
            int day_of_week = c.get(Calendar.DAY_OF_WEEK);
            int add = frequency - day_of_week;
            add = add >= 0 ? add : BIBaseConstant.SIZE7 + add;
            c.add(Calendar.DAY_OF_MONTH, add);
        }
        return c.getTime();
    }

    /**
     * 创建schedule时间
     *
     * @param frequency 频率
     * @return long值
     */
    public static long createScheduleTime(int time, int frequency) {

        if (frequency == DBConstant.UPDATE_FREQUENCY.EVER_DAY) {
            return DateConstant.DATEDELTRA.DAY;
        } else if (frequency == DBConstant.UPDATE_FREQUENCY.EVER_MONTH) {
            return createMonthPeriod(time);
        }
        return DateConstant.DATEDELTRA.WEEK;
    }

    private static long createMonthPeriod(int day) {

        return createMonthStartDate(day).getTime() - new Date().getTime();
    }

    private static Date createMonthStartDate(int day) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        int month = c.get(Calendar.MONTH);
        c.add(Calendar.DAY_OF_MONTH, 1);
        boolean isLastDay = c.get(Calendar.MONTH) != month;
        c.add(Calendar.DAY_OF_MONTH, -1);
        if (c.get(Calendar.DAY_OF_MONTH) < day && !isLastDay) {
            c.set(Calendar.DAY_OF_MONTH, day);     //可能加一个月
            if (c.get(Calendar.DAY_OF_MONTH) != day) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
        } else {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.MONTH, 1);
            c.set(Calendar.DAY_OF_MONTH, day);
            if (c.get(Calendar.DAY_OF_MONTH) != day) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
        }
        return c.getTime();
    }

    public static long toSimpleDay(long t) {

        return toSimpleDay(t, Calendar.getInstance());
    }

    public static long toSimpleDay(long t, Calendar c) {

        c.setTimeInMillis(t);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long toYearMonthDayHour(long t) {

        return toYearMonthDayHour(t, Calendar.getInstance());
    }

    public static long toYearMonthDayHour(long t, Calendar c) {

        c.setTimeInMillis(t);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long toYearMonthDayHourMinute(long t) {

        return toYearMonthDayHourMinute(t, Calendar.getInstance());
    }

    public static long toYearMonthDayHourMinute(long t, Calendar c) {

        c.setTimeInMillis(t);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long toYearMonthDayHourMinuteSecond(long t) {

        return toYearMonthDayHourMinuteSecond(t, Calendar.getInstance());
    }

    public static long toYearMonthDayHourMinuteSecond(long t, Calendar c) {

        c.setTimeInMillis(t);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    //year-month 统一设置为该月份第一天
    public static long toYearMonth(long t) {

        return toYearMonth(t, Calendar.getInstance());
    }

    //year-month 统一设置为该月份第一天
    public static long toYearMonth(long t, Calendar c) {

        c.setTimeInMillis(t);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    //    year-weeknumber 统一设置为该周的第一天
    public static long toYearWeekNumber(long t) {

        return toYearWeekNumber(t, Calendar.getInstance());
    }

    //    year-weeknumber 统一设置为该周的第一天
    public static long toYearWeekNumber(long t, Calendar c) {

        c.setTimeInMillis(t);
        int year = c.get(Calendar.YEAR);
        c.set(Calendar.DAY_OF_WEEK, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (c.get(Calendar.YEAR) != year) {
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH, 1);
        }
        return c.getTimeInMillis();
    }

    public static int toWeekNumber(long t) {

        return toWeekNumber(t, Calendar.getInstance());
    }

    public static int toWeekNumber(long t, Calendar c) {

        c.setTimeInMillis(t);
        int resultWeekNumber = c.get(Calendar.WEEK_OF_YEAR);
        int year = c.get(Calendar.YEAR);
        c.add(Calendar.DAY_OF_MONTH, (-1 * DateConstant.CALENDAR.WEEK.SUNDAY_7));
        if (resultWeekNumber < c.get(Calendar.WEEK_OF_YEAR) && (c.get(Calendar.YEAR) == year)) {
            resultWeekNumber = c.get(Calendar.WEEK_OF_YEAR) + 1;
        }
        return resultWeekNumber;
    }

    public static long toYearSeason(long t) {

        return toYearSeason(t, Calendar.getInstance());
    }

    //    year-season 统一设置为该季度的第一个月的第一天
    public static long toYearSeason(long t, Calendar c) {

        c.setTimeInMillis(t);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        int month = (BITimeUtils.getFieldFromTime(t, Calendar.MONTH) / 3) * 3;
        c.set(Calendar.MONTH, month);
        return c.getTimeInMillis();
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDateTime() {

        return DateUtils.DATETIMEFORMAT2.format(new Date());
    }

    public static void checkDateFieldType(Map<BIKey, ? extends ICubeFieldSource> map, BIKey key) {

        ICubeFieldSource field = map.get(key);
        if (field == null || field.getFieldType() != DBConstant.COLUMN.DATE) {
            throw NOT_DATE_FIELD_EXCEPTION;
        }
    }

    public static final RuntimeException NOT_DATE_FIELD_EXCEPTION = new RuntimeException("not date field");

    public static GroupValueIndex createFilterIndex(ICubeColumnIndexReader yearMap, ICubeColumnIndexReader monthMap, ICubeColumnIndexReader dayMap, BIDay start, BIDay end) {

        return new RangeIndexGetter(yearMap, monthMap, dayMap).createRangeIndex(start, end);
    }

    public static String getScheduleTime(int time, int frequency) {

        String scheduleTime;
        switch (frequency) {
            case DBConstant.UPDATE_FREQUENCY.EVER_MONTH:
                scheduleTime = "0 0 0 " + time + " * ?";
                break;
            case DBConstant.UPDATE_FREQUENCY.EVER_DAY:
                scheduleTime = "0 0 " + time + " * * ?";
                break;
            //每周几
            default:
                scheduleTime = "0 0 " + time + " ? * " + frequency;
        }
        return scheduleTime;
    }

    public static Object firstDate(ICubeColumnIndexReader column) {

        if (column != null) {
            Iterator<Map.Entry<Object, GroupValueIndex>> iter = column.iterator();
            Object v = null;
            while (iter.hasNext()) {
                v = iter.next().getKey();
                if (BICollectionUtils.isNotCubeNullKey(v)) {
                    return v;
                }
            }
        }
        return null;
    }

    public static Object lastDate(ICubeColumnIndexReader column) {

        if (column != null) {
            Iterator<Map.Entry<Object, GroupValueIndex>> iter = column.previousIterator();
            Object v = null;
            while (iter.hasNext()) {
                v = iter.next().getKey();
                if (BICollectionUtils.isNotCubeNullKey(v)) {
                    return v;
                }
            }
        }
        return null;
    }

    /**
     * 获取年周数的
     *
     * @return
     */
    public static int getWeekNumber(long t) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(t);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month == 0 && day <= week) {
            return 1;
        }
        calendar.set(Calendar.DAY_OF_MONTH, day - week);
        long temp = calendar.getTimeInMillis();
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        double o = temp / MILLSIONOFWEEK;
        int offset = (int) Math.floor(o);
        if (calendar.get(Calendar.DAY_OF_WEEK) > 0) {
            offset++;
        }
        return offset;
    }
}
