package com.fr.swift.adaptor.transformer.date;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeOffset;
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

    /**
     * 计算日期过滤器的值
     *
     * @param bean 过滤器属性
     * @param isEndOfDay 某一天最后的时刻，否则为某一天开始的时刻。如果bean为静态日期，该属性不起作用
     * @return
     */
    public static long dateFilterBean2Long(DateFilterBean bean, boolean isEndOfDay) {
        int type = bean.getType();
        switch (type) {
            case BICommonConstants.DATE_TYPE.STATIC:
                return dateStaticFilterBean2Calendar(((DateStaticFilterBean) bean).getValue()).getTimeInMillis();
            case BICommonConstants.DATE_TYPE.DYNAMIC:
                // 动态时间的最小精度为天，日期的范围过滤都要转为某一天的开始或者结束时刻
                long time = dateDynamicFilterBeanValue2Long(((DateDynamicFilterBean) bean).getValue());
                // 比如时间范围过滤器右边选了动态日期的N天前（后），用于过滤的值是这一天的最后一毫秒的时刻
                if (isEndOfDay) {
                    return endOfDay(time);
                } else {
                    return startOfDay(time);
                }
            default:
                return dateStaticFilterBean2Calendar(((DateStaticFilterBean) bean).getValue()).getTimeInMillis();
        }
    }

    /**
     * 日期的等于过滤器转为范围过滤
     * long[] = { 当前时间精度的开始时刻，当前时间精度的结束时刻 }
     * @param bean
     * @return
     */
    public static long[] dateEqualFilterBean2Long(DateFilterBean bean) {
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
        if (type == BICommonConstants.DATE_TYPE.DYNAMIC && ((DateDynamicFilterBean) bean).getValue().getPosition() != 0) {
            // 当天、初、末的时间精度都是DAY
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(range[0]);
            range[0] = startOfDay(calendar).getTimeInMillis();
            range[1] = endOfDay(calendar).getTimeInMillis();
        }
        return range;
    }

    public static long[] dateOffset2Range(long time, DateRangeOffset offset) {
        return null;
    }

    private static TimePrecision timePrecisionOfDateRangeOffset(DateRangeOffset offset) {
        return null;
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
        } else if (value.getDay() != null) {
            return TimePrecision.DAY;
        } else if (value.getMonth() != null) {
            return TimePrecision.MONTH;
        } else if (value.getQuarter() != null) {
            return TimePrecision.QUARTER;
        } else {
            // 这个情况是dashboard的年份过滤（不能选position的，position为默认值0）
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

    private static long dateDynamicFilterBeanValue2Long(DateDynamicFilterBeanValue value) {
        Calendar c = dateDynamicFilterBeanValue2Calendar(value);
        int position = value.getPosition();
        if (position != 2 && position != 3) {
            return c.getTimeInMillis();
        }
        TimeType timeType = getUnit(value, position);
        switch (timeType) {
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

    private static TimeType getUnit(DateDynamicFilterBeanValue value, int position) {
        if (value.getWeek() != null) {
            return position == 2 ? TimeType.WEEK_START : TimeType.WEEK_END;
        }
        if (value.getMonth() != null) {
            return position == 2 ? TimeType.MONTH_START : TimeType.MONTH_END;
        }
        if (value.getQuarter() != null) {
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

    private static long startOfDay(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return startOfDay(calendar).getTimeInMillis();
    }

    private static long endOfDay(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return endOfDay(calendar).getTimeInMillis();
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
        WEEK_START, WEEK_END
    }

    private enum TimePrecision {
        YEAR, QUARTER, MONTH, WEEK, DAY
    }
}
