package com.fr.swift.cloud.util;

import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Heng.J
 * @date 2020/11/10
 * @description
 * @since swift-1.2.0
 */
public class TimeUtils {

    //TODO : 2020/12/22 把这个和cloud的整合一下
    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("^20[0-9]{2}(0[1-9]|1[012])$");

    private static final Pattern YEAR_MONTH_DATE_PATTERN = Pattern.compile("^20[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$");

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    public static boolean isYearMonth(String value) {
        return YEAR_MONTH_PATTERN.matcher(value).matches();
    }

    public static boolean isYearMonthDate(String value) {
        return YEAR_MONTH_DATE_PATTERN.matcher(value).matches();
    }

    public static YearMonth strToYearMonth(String yearMonth) {
        return YearMonth.parse(yearMonth, YEAR_MONTH_FORMATTER);
    }

    public static String ymToString(YearMonth yearMonth) {
        return yearMonth.format(YEAR_MONTH_FORMATTER);
    }

    public static String addOneMonth(String yearMonth) {
        return ymToString(strToYearMonth(yearMonth).plus(Period.ofMonths(1)));
    }

    public static Date firstTimeOfDay(Date currentTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND, 0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
