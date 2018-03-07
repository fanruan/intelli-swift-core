package com.fr.swift.source.etl.utils;

import com.fr.stable.StringUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * This class created on 2016/4/25.
 *
 * @author Connery
 * @since 4.0
 */
public class DateUtils {

    private static String defaultDatePattern = "yyyy-MM-dd";

    /**
     * 获得默认的 date pattern
     */
    public static String getDatePattern() {

        return defaultDatePattern;
    }

    /**
     * 返回预设Format的当前日期字符串
     */
    public static String getToday() {

        Date today = new Date();
        return format(today);
    }

    /**
     * 使用预设Format格式化Date成字符串
     */
    public static String format(Date date) {

        return date == null ? " " : format(date, getDatePattern());
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String format(Date date, String pattern) {

        return date == null ? " " : new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 使用预设格式将字符串转为Date
     */
    public static Date parse(String strDate) throws ParseException {

        return StringUtils.isBlank(strDate) ? null : parse(strDate,
                getDatePattern());
    }

    /**
     * 使用参数Format将字符串转为Date
     */
    public static Date parse(String strDate, String pattern)
            throws ParseException {

        return StringUtils.isBlank(strDate) ? null : new SimpleDateFormat(pattern).parse(strDate);
    }

    /**
     * 在日期上增加数个整月
     */
    public static Date addMonth(Date date, int n) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    public static int getLastDayOfMonth(int year, int month) {

        Calendar cal = Calendar.getInstance();
        // 年
        cal.set(Calendar.YEAR, year);
        // 月份加一，得到下个月的一号(超过11会自动年份加1的，比如2011设置month=12会变成2012年1月)
        cal.set(Calendar.MONTH, month + 1);
        // 日，设为一号
        cal.set(Calendar.DAY_OF_MONTH, 1);
        // 下一个月减一为本月最后一天
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.get(Calendar.DAY_OF_MONTH);// 获得月末是几号
    }

    public static Date getDate(String year, String month, String day)
            throws ParseException {

        String result = year + "- "
                + (month.length() == 1 ? ("0 " + month) : month) + "- "
                + (day.length() == 1 ? ("0 " + day) : day);
        return parse(result);
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * 前面的减后面的
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {

        if (date1 == null || date2 == null) {
            return Integer.MAX_VALUE;
        }
        int days = (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(long date1, long date2) {

        Date d1 = new Date(date1);
        Date d2 = new Date(date2);
        return differentDaysByMillisecond(d1, d2);
    }

    /**
     * 下一个季度 季度表示为 2012-01-01,2016-04-01
     *
     * @param st
     * @return
     */
    public static Date nextYearSeason(long st) {

        Date n = new Date(st);
        Date r = new Date(st);
        int y = n.getYear();
        int m = n.getMonth();
        int s = getSeasonByMonth(m);
        // 第四个季度
        if (s == 3) {
            s = 0;
            // 下一年
            y += 1;
        } else {
            s += 1;
        }
        m = s * 3;
        r.setYear(y);
        r.setMonth(m);
        // 1号
        r.setDate(1);
        return r;
    }

    /**
     * 上一个季度 季度表示为 2012-01-01,2016-04-01
     *
     * @param st
     * @return
     */
    public static Date lastYearSeason(long st) {

        Date n = new Date(st);
        Date r = new Date(st);
        int y = n.getYear();
        int m = n.getMonth();
        int s = getSeasonByMonth(m);
        // 第一季度
        if (s == 0) {
            // 下一年
            y -= 1;
            // 第四季度
            s = 3;
        } else {
            // 上一季度
            s -= 1;
        }
        m = s * 3;
        r.setYear(y);
        r.setMonth(m);
        // 1号
        r.setDate(1);
        return r;
    }

    /**
     * 根据月份获取月份所在的季节
     * 0-->第一季度
     *
     * @param month
     * @return
     */
    public static int getSeasonByMonth(int month) {

        if (month < 3) {
            return 0;
        } else if (month < 6) {
            return 1;
        } else if (month < 9) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * 两个时间是否在相同的年季节里面
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean atSameYearSeason(long time1, long time2) {

        Date t1 = new Date(time1);
        Date t2 = new Date(time2);
        return t1.getYear() == t2.getYear() && getSeasonByMonth(t1.getMonth()) == getSeasonByMonth(t2.getMonth());
    }

    /**
     * time1的年季度树是否晚于time2的年季度数
     * time1>time2
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean afterYearSeason(long time1, long time2) {

        Date t1 = new Date(time1);
        Date t2 = new Date(time2);
        int y1 = t1.getYear();
        int y2 = t2.getYear();
        int s1 = getSeasonByMonth(t1.getMonth());
        int s2 = getSeasonByMonth(t2.getMonth());
        // 年份大于,或同一年的季度大于
        return (y1 > y2) || (y1 == y2 && s1 > s2);
    }

    /**
     * 是否在相同的年季节里面
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean sameYearSeason(long time1, long time2) {

        Date t1 = new Date(time1);
        Date t2 = new Date(time2);
        int y1 = t1.getYear();
        int y2 = t2.getYear();
        int s1 = getSeasonByMonth(t1.getMonth());
        int s2 = getSeasonByMonth(t2.getMonth());
        // 年份相同且季节相同
        return (y1 == y2 && s1 == s2);
    }

    /**
     * 下一个年月 季度表示为 2012-01,2016-04
     *
     * @param st
     * @return
     */
    public static Date nextYearMonth(long st) {

        Date n = new Date(st);
        Date r = new Date(st);
        int y = n.getYear();
        int m = n.getMonth();
        if (m == 11) {
            y += 1;
            m = 0;
        } else {
            m += 1;
        }
        r.setYear(y);
        r.setMonth(m);
        return r;
    }

    /**
     * 上一个季度 季度表示为 2012-01-01,2016-04
     *
     * @param st
     * @return
     */
    public static Date lastYearMonth(long st) {

        Date n = new Date(st);
        Date r = new Date(st);
        int y = n.getYear();
        int m = n.getMonth();
        // 一月份
        if (m == 0) {
            m = 11;
            y -= 1;
        } else {
            // 上一个月
            m -= 1;
        }
        r.setYear(y);
        r.setMonth(m);
        return r;
    }

    /**
     * 两个时间是否在相同的年月里面
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean atSameYearMonth(long time1, long time2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
    }

    /**
     * time1的年季度树是否晚于time2的年季度数
     * time1>time2
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean afterYearMonth(long time1, long time2) {

        // 年份大于,或同一年的月份大于
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);
        return c1.get(Calendar.YEAR) > c2.get(Calendar.YEAR)
                || (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) > c2.get(Calendar.MONTH));
    }


    /**
     * 下一个年周
     *
     * @param st
     * @return
     */
    public static Date nextYearWeek(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.WEEK_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * 上一个年周
     *
     * @param st
     * @return
     */
    public static Date lastYearWeek(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        return c.getTime();
    }

    /**
     * 两个时间是否在相同的年月里面
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean atSameYearWeek(long time1, long time2) {

        return compareDate(time1, time2) == 0;
    }


    /**
     * time1的年季度树是否晚于time2的年季度数
     * time1>time2
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean afterYearWeek(long time1, long time2) {

        return compareDate(time1, time2) > 0;
    }

    public static int compareDate(long time1, long time2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);
        return c1.compareTo(c2);
    }

    /**
     * 下一个年月日
     *
     * @param st
     * @return
     */
    public static Date nextYearMonthDay(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * 上一个年月日
     *
     * @param st
     * @return
     */
    public static Date lastYearMonthDay(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.DAY_OF_YEAR, -1);
        return c.getTime();
    }

    public static boolean afterYearMonthDay(long time1, long time2) {

        return compareDate(time1, time2) > 0;
    }

    public static boolean sameYearMonthDay(long time1, long time2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 相同的年月日时
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean sameYearMonthDayHour(long time1, long time2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                && c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 上一个年月日时
     *
     * @param st
     * @return
     */
    public static Date lastYearMonthDayHour(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.HOUR_OF_DAY, -1);
        return c.getTime();
    }

    /**
     * 下一个年月日时
     *
     * @param st
     * @return
     */
    public static Date nextYearMonthDayHour(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.HOUR_OF_DAY, 1);
        return c.getTime();
    }

    /**
     * time1的年月日时晚于time2的年月日时
     * time1>time2
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean afterYearMonthDayHour(long time1, long time2) {

        return compareDate(time1, time2) > 0;
    }


    /**
     * 相同的年月日时分
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean sameYearMonthDayHourMinute(long time1, long time2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                && c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)
                && c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE);
    }

    /**
     * 上一个年月日时分
     *
     * @param st
     * @return
     */
    public static Date lastYearMonthDayHourMinute(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.MINUTE, -1);
        return c.getTime();
    }

    /**
     * 下一个年月日时分
     *
     * @param st
     * @return
     */
    public static Date nextYearMonthDayHourMinute(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.MINUTE, 1);
        return c.getTime();
    }

    /**
     * time1的年月日时分晚于time2的年月日时分
     * time1>time2
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean afterYearMonthDayHourMinute(long time1, long time2) {

        return compareDate(time1, time2) > 0;
    }

    /**
     * 相同的年月日时分秒
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean sameYearMonthDayHourMinuteSecond(long time1, long time2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                && c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)
                && c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE)
                && c1.get(Calendar.SECOND) == c2.get(Calendar.SECOND);
    }

    /**
     * 上一个年月日时分秒
     *
     * @param st
     * @return
     */
    public static Date lastYearMonthDayHourMinuteSecond(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.SECOND, -1);
        return c.getTime();
    }

    /**
     * 下一个年月日时分秒
     *
     * @param st
     * @return
     */
    public static Date nextYearMonthDayHourMinuteSecond(long st) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(st);
        c.add(Calendar.SECOND, 1);
        return c.getTime();
    }

    /**
     * time1的年月日时分秒晚于time2的年月日时分秒
     * time1>time2
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean afterYearMonthDayHourMinuteSecond(long time1, long time2) {

        return compareDate(time1, time2) > 0;
    }

    public static void checkDateColumnType(SwiftMetaDataColumn column) {
        if(null == column || column.getType() != ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.DATE)) {
            throw new RuntimeException("not date field");
        }
    }
}
