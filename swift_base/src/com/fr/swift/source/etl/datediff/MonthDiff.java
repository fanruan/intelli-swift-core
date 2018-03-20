package com.fr.swift.source.etl.datediff;

import com.fr.swift.segment.column.impl.DateType;

import java.util.Calendar;

/**
 * @author Daniel
 *
 */
public class MonthDiff implements DateDiffCalculator {
    public static final MonthDiff INSTANCE = new MonthDiff();
    @Override
    public int get(Long d1, Long d2) {
        if(d1 == null || d2 == null){
            return 0;
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(d2);
        int year = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        int month = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        return year * DateType.MONTH.radix + month;
    }

}
