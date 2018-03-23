package com.fr.swift.adaptor.transformer.date;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.BIConfConstants;
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
        }
        return dateStaticFilterBean2Long(((DateStaticFilterBean) bean).getValue());
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
        c.add(Calendar.YEAR, string2Int(value.getYear()));
        c.add(Calendar.MONTH, string2Int(value.getMonth()));
        c.add(Calendar.MONTH, string2Int(value.getQuarter()) * 3);
        c.add(Calendar.DATE, string2Int(value.getWeek()) * 7);
        c.add(Calendar.DATE, string2Int(value.getWorkDay()));
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
        }
        return c.getTimeInMillis();
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
        // TODO: 2018/3/23 一周从哪天开始，使用默认的？
        c.set(Calendar.DATE, c.getFirstDayOfWeek() - c.get(Calendar.DAY_OF_WEEK));
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

    /**
     * 这边END时间点是不包含的
     * @param type
     * @param value
     * @return
     */
    public static long getTime(int type, int value) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        switch (type) {
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_YEAR_PREV:
                now.add(Calendar.YEAR, -value);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_YEAR_AFTER:
                now.add(Calendar.YEAR, value);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_YEAR_BEGIN: {
                int year = now.get(Calendar.YEAR);
                now.clear();
                now.set(Calendar.YEAR, year);
                return now.getTimeInMillis();
            }
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_YEAR_END: {
                int year = now.get(Calendar.YEAR);
                now.clear();
                now.set(Calendar.YEAR, year);
                now.add(Calendar.YEAR, 1);
                return now.getTimeInMillis();
            }

            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_MONTH_PREV:
                now.add(Calendar.MONTH, -value);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_MONTH_AFTER:
                now.add(Calendar.MONTH, value);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_MONTH_BEGIN: {
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                now.clear();
                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, month);
                return now.getTimeInMillis();
            }
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_MONTH_END: {
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                now.clear();
                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, month);
                now.add(Calendar.MONTH, 1);
                return now.getTimeInMillis();
            }

            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_QUARTER_PREV:
                now.add(Calendar.MONTH, -value * 3);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_QUARTER_AFTER:
                now.add(Calendar.MONTH, value * 3);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_QUARTER_BEGIN: {
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                now.clear();
                int season = month2Quarter(month);
                month = getStartMonthOfQuarter(season);
                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, month);
                return now.getTimeInMillis();
            }
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_QUARTER_END: {
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                now.clear();
                int season = month2Quarter(month);
                month = getStartMonthOfQuarter(season);
                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, month);
                now.add(Calendar.MONTH, 3);
                return now.getTimeInMillis();
            }

            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_WEEK_PREV:
                now.add(Calendar.DATE, -value * 7);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_WEEK_AFTER:
                now.add(Calendar.DATE, value * 7);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_DAY_PREV:
                now.add(Calendar.DATE, -value);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_DAY_AFTER:
                now.add(Calendar.DATE, value);
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_DAY_TODAY:
                return now.getTimeInMillis();
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_PARAM:
        }
        return now.getTimeInMillis();
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
