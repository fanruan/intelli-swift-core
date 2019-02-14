package com.fr.swift.source.etl.datediff;

import com.fr.swift.segment.column.impl.DateType;

/**
 * @author Daniel
 */
public class DayDiff implements DateDiffCalculator {
    public static final DayDiff INSTANCE = new DayDiff();

    private static final int ONE_DAY = DateType.HOUR.radix * DateType.MINUTE.radix * DateType.SECOND.radix * DateType.MILLISECOND.radix;

    @Override
    public Object get(Long d1, Long d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        long t = d1 - d2;
        return (int) (t / ONE_DAY);
    }
}