package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.number.not.IntNotInRangeFilter;

/**
 * Created by Lyon on 2017/12/1.
 */
public class IntNotInRangeFilterTest extends NumberInRangeFilterTest {
    public IntNotInRangeFilterTest() {
        super(intDetails, IntNotInRangeFilter.class, intType, intColumn);
        this.isNot = true;
    }
}
