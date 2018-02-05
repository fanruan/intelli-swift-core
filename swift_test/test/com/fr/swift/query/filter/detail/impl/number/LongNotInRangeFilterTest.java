package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.number.not.LongNotInRangeFilter;

/**
 * Created by Lyon on 2017/12/1.
 */
public class LongNotInRangeFilterTest extends NumberInRangeFilterTest {

    public LongNotInRangeFilterTest() {
        super(longDetails, LongNotInRangeFilter.class, longType, longColumn);
        this.isNot = true;
    }
}
