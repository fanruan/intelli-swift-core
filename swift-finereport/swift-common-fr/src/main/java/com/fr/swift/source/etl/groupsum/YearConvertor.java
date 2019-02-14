package com.fr.swift.source.etl.groupsum;

import com.fr.swift.util.function.Function;

import java.util.Calendar;

/**
 * Created by pony on 2018/4/24.
 */
public class YearConvertor implements Function<Long, Long> {
    private Calendar c = Calendar.getInstance();

    @Override
    public Long apply(Long year) {
        if (year == null) {
            return null;
        }
        c.clear();
        c.set(Calendar.YEAR, year.intValue());
        return c.getTimeInMillis();
    }
}
