package com.fr.swift.adaptor.transformer.date;

import com.finebi.conf.constant.BIConfConstants;

import java.util.Calendar;

/**
 * Created by Lyon on 2018/3/8.
 */
public class DateUtils {

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
                now.set(Calendar.YEAR, year + 1);
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
                now.set(Calendar.MONTH, month + 1);
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
                int season = getSeason(month);
                month = getStartMonthOfSeason(season);
                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, month);
                return now.getTimeInMillis();
            }
            case BIConfConstants.CONF.DATE_TYPE.MULTI_DATE_QUARTER_END: {
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                now.clear();
                int season = getSeason(month);
                month = getStartMonthOfSeason(season + 1);
                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, month);
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

    private static int getStartMonthOfSeason(int season) {
        switch (season) {
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

    private static int getSeason(int month) {
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
}
