package com.fr.swift.source.etl.datediff;

import com.fr.swift.segment.column.impl.DateType;

import java.util.Calendar;

/**
 * @author Daniel
 */
public class MonthDiff implements DateDiffCalculator {
    private Calendar c = Calendar.getInstance();

    @Override
    public Object get(Long d1, Long d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        c.setTimeInMillis(d1);
        int year1 = DateType.YEAR.from(c), month1 = DateType.MONTH.from(c);
        c.setTimeInMillis(d2);
        int year2 = DateType.YEAR.from(c), month2 = DateType.MONTH.from(c);
        int year = year1 - year2;
        int month = month1 - month2;
        return year * DateType.MONTH.radix + month;
    }

}
