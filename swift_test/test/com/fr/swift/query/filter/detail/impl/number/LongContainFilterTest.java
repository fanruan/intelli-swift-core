package com.fr.swift.query.filter.detail.impl.number;

/**
 * Created by Lyon on 2017/12/1.
 */
public class LongContainFilterTest extends NumberContainFilterTest {

    public LongContainFilterTest() {
        super(longDetails);
        this.column = longColumn;
        this.filter = new LongContainFilter(groups, column);
    }
}
