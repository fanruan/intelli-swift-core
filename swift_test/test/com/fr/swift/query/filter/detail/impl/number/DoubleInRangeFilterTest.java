package com.fr.swift.query.filter.detail.impl.number;

/**
 * Created by Lyon on 2017/12/1.
 */
public class DoubleInRangeFilterTest extends NumberInRangeFilterTest {
    public DoubleInRangeFilterTest() {
        super(doubleDetails, DoubleInRangeFilter.class, doubleType, doubleColumn);
    }
}
