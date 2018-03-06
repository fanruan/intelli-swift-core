package com.fr.swift.query.filter.detail.impl.number;

/**
 * Created by Lyon on 2017/12/1.
 */
public class IntContainFilterTest extends NumberContainFilterTest {

    public IntContainFilterTest() {
        super(intDetails);
        this.column = intColumn;
        this.filter = new IntContainFilter(groups, column);
    }
}
