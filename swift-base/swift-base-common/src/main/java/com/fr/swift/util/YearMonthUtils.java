package com.fr.swift.util;

import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * @author Heng.J
 * @date 2020/11/10
 * @description
 * @since swift-1.2.0
 */
public class YearMonthUtils {

    public static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    public static YearMonth strToYearMonth(String yearMonth) {
        return YearMonth.parse(yearMonth, yearMonthFormatter);
    }

    public static String ymToString(YearMonth yearMonth) {
        return yearMonth.format(yearMonthFormatter);
    }

    public static String addOneMonth(String yearMonth) {
        return ymToString(strToYearMonth(yearMonth).plus(Period.ofMonths(1)));
    }
}
