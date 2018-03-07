package com.fr.swift.source.etl.datediff;

import com.fr.swift.source.etl.utils.ETLConstant;

/**
 * @author Daniel
 *
 */
public class DayDiff implements DateDiffCalculator {

    public static final DayDiff INSTANCE = new DayDiff();

    @Override
    public int get(Long d1, Long d2) {
        if(d1 == null || d2 == null){
            return 0;
        }
        long t = d1.longValue() - d2.longValue();
        return (int) (t / ETLConstant.CONF.DATEDELTRA.DAY);
    }
}