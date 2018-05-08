package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.number.not.NumberNotInRangeFilter;

/**
 * Created by Lyon on 2017/12/1.
 */
public class NumberNotInRangeFilterTest extends NumberInRangeFilterTest {

    public NumberNotInRangeFilterTest() {
        this.isNot = true;
    }

    @Override
    public void setUp() throws Exception {
        this.filterClass = NumberNotInRangeFilter.class;
    }
}
