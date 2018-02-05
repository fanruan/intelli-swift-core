package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.number.not.DoubleNotInRangeFilter;

/**
 * Created by Lyon on 2017/12/1.
 */
public class DoubleNotInRangeFilterTest extends NumberInRangeFilterTest {

    public DoubleNotInRangeFilterTest() {
        super(doubleDetails, DoubleNotInRangeFilter.class, doubleType, doubleColumn);
        this.isNot = true;
    }
}
