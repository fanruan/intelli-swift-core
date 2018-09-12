package com.fr.swift.source.etl.datediff;

/**
 * @author anchore
 * @date 2018/3/20
 */
public class WeekDiffer extends DayDiff {
    @Override
    public Object get(Long d1, Long d2) {
        Object dayDiff = super.get(d1, d2);
        if (null != dayDiff) {
            return Integer.parseInt(dayDiff.toString()) / 7;
        }
        return null;
    }
}