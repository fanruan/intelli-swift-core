package com.fr.swift.query.filter.detail.impl.number;

/**
 * Created by Lyon on 2017/12/1.
 */
public class LongInRangeFilterTest extends NumberInRangeFilterTest {

    public LongInRangeFilterTest() {
        super(longDetails, LongInRangeFilter.class, longType, longColumn);
    }
}
