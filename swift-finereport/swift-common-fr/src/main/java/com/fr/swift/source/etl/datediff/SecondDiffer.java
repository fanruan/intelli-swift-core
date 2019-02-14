package com.fr.swift.source.etl.datediff;

import com.fr.swift.segment.column.impl.DateType;

/**
 * @author anchore
 * @date 2018/3/19
 */
public class SecondDiffer implements DateDiffCalculator {
    public static final DateDiffCalculator INSTANCE = new SecondDiffer();

    private static final int ONE_SECOND = DateType.MILLISECOND.radix;

    @Override
    public Object get(Long d1, Long d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        return (int) ((d1 - d2) / ONE_SECOND);
    }
}