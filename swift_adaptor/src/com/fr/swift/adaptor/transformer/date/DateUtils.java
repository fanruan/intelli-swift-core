package com.fr.swift.adaptor.transformer.date;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateDynamicFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateDynamicFilterBeanValue;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBeanValue;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.fr.stable.StringUtils;

import java.util.Calendar;

/**
 * Created by Lyon on 2018/3/8.
 */
public class DateUtils {

    public static long dateFilterBean2Long(DateFilterBean bean) {
        int type = bean.getType();
        switch (type) {
            case BICommonConstants.DATE_TYPE.STATIC:
                return dateStaticFilterBean2Long(((DateStaticFilterBean) bean).getValue());
            case BICommonConstants.DATE_TYPE.DYNAMIC:
                return dateDynamicFilterBeanValue2Long(((DateDynamicFilterBean) bean).getValue());
            default:
                return dateStaticFilterBean2Long(((DateStaticFilterBean) bean).getValue());
        }
    }

    public static long dateStaticFilterBean2Long(DateStaticFilterBeanValue value) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, value.getYear());
        // TODO: 2018/3/23 季度这个值没法理解
//                c.set(Calendar.MONTH, getStartMonthOfQuarter(value.month2Quarter()));
        c.set(Calendar.MONTH, value.getMonth());
        c.set(Calendar.DATE, value.getDay());
        c.set(Calendar.HOUR_OF_DAY, value.getHour());
        c.set(Calendar.MINUTE, value.getMinute());
        c.set(Calendar.SECOND, value.getSecond());
        return c.getTimeInMillis();
    }

    public static long dateDynamicFilterBeanValue2Long(DateDynamicFilterBeanValue value) {
        Calendar c = Calendar.getInstance();
        // workDay和其他几个属性互斥
        if (value.getWorkDay() == null) {
            c.add(Calendar.YEAR, string2Int(value.getYear()));
            c.add(Calendar.MONTH, string2Int(value.getMonth()));
            c.add(Calendar.MONTH, string2Int(value.getQuarter()) * 3);
            c.add(Calendar.DATE, string2Int(value.getWeek()) * 7);
            c.add(Calendar.DATE, string2Int(value.getDay()));
        } else {
            c.add(Calendar.DATE, workDayOffset2DayOffset(string2Int(value.getWorkDay())));
        }
        int position = value.getPosition();
        if (position != 2 || position != 3) {
            return c.getTimeInMillis();
        }
        TimeType timeType = getUnit(value, position);
        switch (timeType) {
            case DAY_START:
                return startOfDay(c).getTimeInMillis();
            case DAY_END:
                return endOfDay(c).getTimeInMillis();
            case WEEK_START:
                return startOfWeek(c).getTimeInMillis();
            case WEEK_END:
                return endOfWeek(c).getTimeInMillis();
            case MONTH_START:
                return startOfMonth(c).getTimeInMillis();
            case MONTH_END:
                return endOfMonth(c).getTimeInMillis();
            case QUARTER_START:
                return startOfQuarter(c).getTimeInMillis();
            case QUARTER_END:
                return endOfQuarter(c).getTimeInMillis();
            case YEAR_START:
                return startOfYear(c).getTimeInMillis();
            case YEAR_END:
                return endOfYear(c).getTimeInMillis();
            default:
                return c.getTimeInMillis();
        }
    }

    // 一周工作日的起始日和结束日，没有功能文档，和测试同学统一的
    private static int START_OF_WORK_DAY = Calendar.MONDAY;
    private static int END_OF_WORK_DAY = Calendar.FRIDAY;

    private static int workDayOffset2DayOffset(int workDayOffset) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        boolean after = workDayOffset > 0;
        int dayOffset;
        if (after) {
            int n = workDayOffset - (END_OF_WORK_DAY - dayOfWeek);
            dayOffset = n <= 0 ? workDayOffset : (END_OF_WORK_DAY - dayOfWeek) + (n % 5 == 0 ? n / 5 : n / 5 + 1) * 2 + n;
        } else {
            int n = -workDayOffset - (dayOfWeek - START_OF_WORK_DAY);
            dayOffset = n <= 0 ? workDayOffset : (START_OF_WORK_DAY - dayOfWeek) - (n % 5 == 0 ? n / 5 : n / 5 + 1) * 2 - n;
        }
        return dayOffset;
    }

    private static TimeType getUnit(DateDynamicFilterBeanValue value, int position) {
        if (value.getWorkDay() == null) {
            return position == 2 ? TimeType.DAY_START : TimeType.DAY_END;
        }
        if (value.getWeek() == null) {
            return position == 2 ? TimeType.WEEK_START : TimeType.WEEK_END;
        }
        if (value.getMonth() == null) {
            return position == 2 ? TimeType.MONTH_START : TimeType.MONTH_END;
        }
        if (value.getQuarter() == null) {
            return position == 2 ? TimeType.QUARTER_START : TimeType.QUARTER_END;
        }
        return position == 2 ? TimeType.YEAR_START : TimeType.YEAR_END;
    }

    private static Calendar startOfYear(Calendar c) {
        int year = c.get(Calendar.YEAR);
        c.clear();
        c.set(Calendar.YEAR, year);
        return c;
    }

    private static Calendar endOfYear(Calendar c) {
        Calendar calendar = startOfYear(c);
        calendar.add(Calendar.YEAR, 1);
        return minus1Millisecond(calendar);
    }

    private static Calendar startOfMonth(Calendar c) {
        int month = c.get(Calendar.MONTH);
        Calendar calendar = startOfYear(c);
        calendar.set(Calendar.MONTH, month);
        return calendar;
    }

    private static Calendar endOfMonth(Calendar c) {
        Calendar calendar = startOfMonth(c);
        calendar.add(Calendar.MONTH, 1);
        return minus1Millisecond(calendar);
    }

    private static Calendar startOfQuarter(Calendar c) {
        int month = c.get(Calendar.MONTH);
        int quarter = month2Quarter(month);
        c.set(Calendar.MONTH, getStartMonthOfQuarter(quarter));
        return startOfMonth(c);
    }

    private static Calendar endOfQuarter(Calendar c) {
        Calendar calendar = startOfQuarter(c);
        calendar.add(Calendar.MONTH, 2);
        return endOfMonth(c);
    }

    private static Calendar startOfWeek(Calendar c) {
        c.set(Calendar.DATE, c.getFirstDayOfWeek() - c.get(Calendar.DAY_OF_WEEK));
        return startOfDay(c);
    }

    private static Calendar endOfWeek(Calendar c) {
        Calendar calendar = startOfWeek(c);
        calendar.add(Calendar.DATE, 6);
        return endOfDay(c);
    }

    public static long startOfDay(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return startOfDay(calendar).getTimeInMillis();
    }

    public static long endOfDay(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return endOfDay(calendar).getTimeInMillis();
    }

    public static long endOfLastDay(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        calendar.add(Calendar.DATE, -1);
        return endOfDay(calendar).getTimeInMillis();
    }

    public static long startOfNextDay(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        calendar.add(Calendar.DATE, 1);
        return startOfDay(calendar).getTimeInMillis();
    }

    private static Calendar startOfDay(Calendar c) {
        int day = c.get(Calendar.DATE);
        Calendar calendar = startOfMonth(c);
        calendar.set(Calendar.DATE, day);
        return calendar;
    }

    private static Calendar endOfDay(Calendar c) {
        Calendar calendar = startOfDay(c);
        calendar.add(Calendar.DATE, 1);
        return minus1Millisecond(c);
    }

    private static int string2Int(String str) {
        return StringUtils.isNotEmpty(str) ? Integer.parseInt(str.trim()) : 0;
    }

    private static Calendar minus1Millisecond(Calendar c) {
        c.add(Calendar.MILLISECOND, -1);
        return c;
    }

    private static int getStartMonthOfQuarter(int quarter) {
        switch (quarter) {
            case 1:
                return Calendar.JANUARY;
            case 2:
                return Calendar.APRIL;
            case 3:
                return Calendar.JULY;
            default:
                return Calendar.OCTOBER;
        }
    }

    private static int month2Quarter(int month) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                return 1;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                return 2;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                return 3;
            default:
                return 4;
        }
    }

    private enum TimeType {
        YEAR_START, YEAR_END,
        QUARTER_START, QUARTER_END,
        MONTH_START, MONTH_END,
        WEEK_START, WEEK_END,
        DAY_START, DAY_END
    }
}
