package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.number.not.DoubleNotContainFilter;

/**
 * Created by Lyon on 2017/12/1.
 */
public class DoubleNotContainFilterTest extends DoubleContainFilterTestTest {

    public DoubleNotContainFilterTest() {
        this.filter = new DoubleNotContainFilter(details.size(), groups, column);
        this.isNot = true;
    }
}
