package com.fr.swift.query.filter.detail.impl.number;

/**
 * Created by Lyon on 2017/12/1.
 */
public class DoubleContainFilterTestTest extends NumberContainFilterTest {

    public DoubleContainFilterTestTest() {
        super(doubleDetails);
        this.column = doubleColumn;
        this.filter = new DoubleContainFilter(groups, column);
    }
}
