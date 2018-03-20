package com.fr.swift.source.etl.datediff;

import com.fr.swift.segment.column.impl.DateType;

/**
 * @author Daniel
 */
public class SeasonDiff extends MonthDiff {
    public static final SeasonDiff INSTANCE = new SeasonDiff();

    @Override
    public int get(Long d1, Long d2) {
        int month = super.get(d1, d2);
        return month / (DateType.MONTH.radix / DateType.QUARTER.radix);
    }
}
