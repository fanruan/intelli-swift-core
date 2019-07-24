package com.fr.swift.cloud.util;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * This class created on 2019/5/27
 *
 * @author Lucifer
 * @description
 */
public class TimeUtils {

    private static DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    public static Date yearMonth2Date(String yearMonthStr) {
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, yearMonthFormatter);
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearMonth.getYear(), yearMonth.getMonthValue() - 1, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
