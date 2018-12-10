package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.InFilter;
import com.fr.swift.query.filter.detail.impl.NotFilter;
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
        return new NotFilter(details.size(), new InFilter(groups, column));
    }
}
