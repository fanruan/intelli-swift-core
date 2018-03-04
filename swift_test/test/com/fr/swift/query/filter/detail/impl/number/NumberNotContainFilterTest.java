package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.number.not.NumberNotContainFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2018/2/28.
 */
public class NumberNotContainFilterTest extends NumberContainFilterTest {

    public NumberNotContainFilterTest() {
        this.isNot = true;
    }

    @Override
    protected DetailFilter createFilter(Column column) {
        return new NumberNotContainFilter(details.size(), groups, column);
    }
}
