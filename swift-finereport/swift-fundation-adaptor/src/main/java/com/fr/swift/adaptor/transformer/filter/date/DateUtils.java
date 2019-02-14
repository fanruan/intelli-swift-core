package com.fr.swift.adaptor.transformer.filter.date;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeOffset;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateDynamicFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateDynamicFilterBeanValue;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBean;
import com.finebi.conf.internalimp.bean.filtervalue.date.single.DateStaticFilterBeanValue;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.fr.stable.StringUtils;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;

import java.util.Calendar;

/**
 * Created by Lyon on 2018/3/8.
 */
public class DateUtils {

    // 某个日期单位时间段的某一天
    static final int POSITION_DAY = 1;
    // 某个日期单位时间段的起始时刻，年初、月初等
    static final int POSITION_START = 2;
    // 某个日期单位时间段的结束时刻，年末、月末等
    static final int POSITION_END = 3;

    /**
     * 计算日期过滤器的值
     *
     * @param bean 属性
     * @param isStartOfPeriod 是否为一段时间的开始时刻
     * @return
     */
    public static long dateFilterBean2Long(DateFilterBean bean, boolean isStartOfPeriod) {
        long[] range = rangeOfDateFilterBean(bean);
        return isStartOfPeriod ? range[0] : range[1];
    }

    public static SwiftDateInRangeFilterValue create(DateFilterBean bean) {
        if (bean == null) {
            return new SwiftDateInRangeFilterValue();
        }
        long[] range = rangeOfDateFilterBean(bean);
        SwiftDateInRangeFilterValue value = new SwiftDateInRangeFilterValue();
        value.setStart(range[0]);
        value.setEnd(range[1]);
        return value;
    }

    /**
     * 日期的等于过滤器转为范围过滤
     * long[] = { 当前时间精度的开始时刻，当前时间精度的结束时刻 }
     * @param bean
     * @return
     */
    public static long[] rangeOfDateFilterBean(DateFilterBean bean) {
        TimePrecision precision;
        Calendar c;
        int type = bean.getType();
        if (type == BICommonConstants.DATE_TYPE.STATIC) {
            precision = timePrecisionOfStaticValue(((DateStaticFilterBean) bean).getValue());
            c = dateStaticFilterBean2Calendar(((DateStaticFilterBean) bean).getValue());
        } else {
            precision = timePrecisionOfDynamicValue(((DateDynamicFilterBean) bean).getValue());
            c = dateDynamicFilterBeanValue2Calendar(((DateDynamicFilterBean) bean).getValue());
        }
        long[] range = new long[2];
        switch (precision) {
            case DAY:
                range[0] = startOfDay(c).getTimeInMillis();
                range[1] = endOfDay(c).getTimeInMillis();
                break;
            case WEEK:
                range[0] = startOfWeek(c).getTimeInMillis();
                range[1] = endOfWeek(c).getTimeInMillis();
                break;
            case MONTH:
                range[0] = startOfMonth(c).getTimeInMillis();
                range[1] = endOfMonth(c).getTimeInMillis();
                break;
            case QUARTER:
                range[0] = startOfQuarter(c).getTimeInMillis();
                range[1] = endOfQuarter(c).getTimeInMillis();
                break;
            case YEAR:
                range[0] = startOfYear(c).getTimeInMillis();
                range[1] = endOfYear(c).getTimeInMillis();
        }
        if (type == BICommonConstants.DATE_TYPE.DYNAMIC) {
            int position = ((DateDynamicFilterBean) bean).getValue().getPosition();
            // 当天
            if (position != POSITION_START && position != POSITION_END) {
                return range;
            }
            // 初、末的时间精度都是DAY
            Calendar calendar = Calendar.getInstance();
            if (position == POSITION_START) {
                calendar.setTimeInMillis(range[0]);
                range[0] = startOfDay(calendar).getTimeInMillis();
                range[1] = endOfDay(calendar).getTimeInMillis();
                return range;
            } else {
                calendar.setTimeInMillis(range[1]);
                range[0] = startOfDay(calendar).getTimeInMillis();
                range[1] = endOfDay(calendar).getTimeInMillis();
                return range;
            }

        }
        return range;
    }

    public static long dateOffset2long(long time, DateRangeOffset offset) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c = setOffset2Calendar(c, offset);
        return c.getTimeInMillis();
    }

    public static long[] dateOffset2Range(long time, DateRangeOffset offset) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c = setOffset2Calendar(c, offset);
        TimePrecision precision = timePrecisionOfDateRangeOffset(offset);
        long[] range = new long[2];
        switch (precision) {
            case DAY:
                range[0] = startOfDay(c).getTimeInMillis();
                range[1] = endOfDay(c).getTimeInMillis();
                break;
            case WEEK:
                range[0] = startOfWeek(c).getTimeInMillis();
                range[1] = endOfWeek(c).getTimeInMillis();
                break;
            case MONTH:
                range[0] = startOfMonth(c).getTimeInMillis();
                range[1] = endOfMonth(c).getTimeInMillis();
                break;
            case QUARTER:
                range[0] = startOfQuarter(c).getTimeInMillis();
                range[1] = endOfQuarter(c).getTimeInMillis();
                break;
            case YEAR:
                range[0] = startOfYear(c).getTimeInMillis();
                range[1] = endOfYear(c).getTimeInMillis();
                break;
        }
        int position = offset.getPosition();
        if (position == POSITION_START) {
            // 年初，月初等，取年初、月初这一天的起始时刻
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(range[0]);
            range[0] = startOfDay(calendar).getTimeInMillis();
            range[1] = endOfDay(calendar).getTimeInMillis();
            return range;
        }
        if (position == POSITION_END) {
            // 年末，月末等，取年末，月末这一天的起始时刻
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(range[1]);
            range[0] = startOfDay(calendar).getTimeInMillis();
            range[1] = endOfDay(calendar).getTimeInMillis();
            return range;
        }
        return range;
    }

    private static Calendar setOffset2Calendar(Calendar c, DateRangeOffset offset) {
        c.add(Calendar.YEAR, string2Int(offset.getYear()));
        c.add(Calendar.MONTH, string2Int(offset.getMonth()));
        c.add(Calendar.MONTH, string2Int(offset.getQuarter()) * 3);
        c.add(Calendar.DATE, string2Int(offset.getWeek()) * 7);
        c.add(Calendar.DATE, string2Int(offset.getDay()));
        return c;
    }

    @SuppressWarnings("Duplicates")
    private static TimePrecision timePrecisionOfDateRangeOffset(DateRangeOffset offset) {
        if (offset.getDay() != null) {
            return TimePrecision.DAY;
        } else if (offset.getWeek() != null) {
            return TimePrecision.WEEK;
        } else if (offset.getMonth() != null) {
            return TimePrecision.MONTH;
        } else if (offset.getQuarter() != null) {
            return TimePrecision.QUARTER;
        } else {
            return TimePrecision.YEAR;
        }
    }

    @SuppressWarnings("Duplicates")
    private static TimePrecision timePrecisionOfStaticValue(DateStaticFilterBeanValue value) {
        if (value.getDay() != null) {
            return TimePrecision.DAY;
        } else if (value.getMonth() != null) {
            return TimePrecision.MONTH;
        } else if (value.getQuarter() != null) {
            return TimePrecision.QUARTER;
        } else {
            return TimePrecision.YEAR;
        }
    }

    @SuppressWarnings("Duplicates")
    private static TimePrecision timePrecisionOfDynamicValue(DateDynamicFilterBeanValue value) {
        if (value.getWorkDay() != null) {
            return TimePrecision.DAY;
        } else if (value.getDay() != null || value.getPosition() == POSITION_DAY) {
            return TimePrecision.DAY;
        } else if (value.getWeek() != null) {
            return TimePrecision.WEEK;
        } else if (value.getMonth() != null) {
            return TimePrecision.MONTH;
        } else if (value.getQuarter() != null) {
            return TimePrecision.QUARTER;
        } else {
            return TimePrecision.YEAR;
        }
    }

    private static Calendar dateStaticFilterBean2Calendar(DateStaticFilterBeanValue value) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, string2Int(value.getYear()));
        c.set(Calendar.MONTH, value.getQuarter() == null ? 0 : getStartMonthOfQuarter(string2Int(value.getQuarter())));
        // 功能的月份从1开始
        c.set(Calendar.MONTH, value.getMonth() == null ? c.get(Calendar.MONTH) : string2Int(value.getMonth()) - 1);
        // Calendar.DATE中月的第一天为1
        c.set(Calendar.DATE, value.getDay() == null ? 1 : string2Int(value.getDay()));
        c.set(Calendar.HOUR_OF_DAY, string2Int(value.getHour()));
        c.set(Calendar.MINUTE, string2Int(value.getMinute()));
        c.set(Calendar.SECOND, string2Int(value.getSecond()));
        return c;
    }

    private static Calendar dateDynamicFilterBeanValue2Calendar(DateDynamicFilterBeanValue value) {
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
        return c;
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

    // 和测试同学确认的
    private static int START_OF_WEEK = Calendar.MONDAY;
    private static int END_OF_WEEK = Calendar.SUNDAY;

    private static int convertDayOfWeek(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            default:
                return 7;

        }
    }

    private static Calendar startOfWeek(Calendar c) {
        c.add(Calendar.DATE, convertDayOfWeek(START_OF_WEEK) - convertDayOfWeek(c.get(Calendar.DAY_OF_WEEK)));
        return startOfDay(c);
    }

    private static Calendar endOfWeek(Calendar c) {
        Calendar calendar = startOfWeek(c);
        calendar.add(Calendar.DATE, 6);
        return endOfDay(c);
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

    private enum TimePrecision {
        YEAR, QUARTER, MONTH, WEEK, DAY
    }
}
