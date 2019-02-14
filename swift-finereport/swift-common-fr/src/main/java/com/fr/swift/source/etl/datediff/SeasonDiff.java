package com.fr.swift.source.etl.datediff;

import com.fr.swift.segment.column.impl.DateType;

/**
 * @author Daniel
 */
public class SeasonDiff extends MonthDiff {
    @Override
    public Object get(Long d1, Long d2) {
        Object month = super.get(d1, d2);
        if (null != month) {
            return Integer.parseInt(month.toString()) / (DateType.MONTH.radix / DateType.QUARTER.radix);
        }
        return null;
    }
}
