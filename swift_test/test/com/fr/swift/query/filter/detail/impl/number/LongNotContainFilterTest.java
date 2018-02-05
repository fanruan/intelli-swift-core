package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.number.not.LongNotContainFilter;

/**
 * Created by Lyon on 2017/12/1.
 */
public class LongNotContainFilterTest extends LongContainFilterTest {

    public LongNotContainFilterTest() {
        this.filter = new LongNotContainFilter(details.size(), groups, column);
        this.isNot = true;
    }
}
