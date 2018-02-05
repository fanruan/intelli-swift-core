package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.number.not.IntNotContainFilter;

/**
 * Created by Lyon on 2017/12/1.
 */
public class IntNotContainFilterTest extends IntContainFilterTest {

    public IntNotContainFilterTest() {
        this.filter = new IntNotContainFilter(details.size(), groups, column);
        this.isNot = true;
    }
}
