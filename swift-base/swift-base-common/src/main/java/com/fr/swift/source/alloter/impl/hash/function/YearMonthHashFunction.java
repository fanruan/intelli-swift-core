package com.fr.swift.source.alloter.impl.hash.function;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * @author lucifer
 * @date 2020/2/11
 * @description
 * @since ness engine demo
 */
public class YearMonthHashFunction implements HashFunction {

    private static DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public int indexOf(Object key) {
        String yearMonthStr = String.valueOf(key);
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, yearMonthFormatter);
        return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
    }

    @Override
    public HashType getType() {
        return HashType.YEAR_MONTH;
    }
}
