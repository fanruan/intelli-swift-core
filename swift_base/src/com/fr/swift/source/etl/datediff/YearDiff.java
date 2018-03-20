package com.fr.swift.source.etl.datediff;

import com.fr.swift.segment.column.impl.DateType;

import java.util.Calendar;

/**
 * @author Daniel
 */
public class YearDiff implements DateDiffCalculator {
    public static final YearDiff INSTANCE = new YearDiff();

    @Override
    public int get(Long d1, Long d2) {
        if (d1 == null || d2 == null) {
            return 0;
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(d2);
        return DateType.YEAR.from(c1) - DateType.YEAR.from(c2);
    }

}
