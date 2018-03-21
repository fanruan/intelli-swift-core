package com.fr.swift.source.etl.datediff;

/**
 * @author anchore
 * @date 2018/3/20
 */
public class WeekDiffer extends DayDiff {
    @Override
    public int get(Long d1, Long d2) {
        return super.get(d1, d2) / 7;
    }
}