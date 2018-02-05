package com.fr.swift.query.filter.detail.impl.number.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.number.LongContainFilter;
import com.fr.swift.segment.column.Column;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/28.
 */
public class LongNotContainFilter extends AbstractNotOperatorFilter {

    public LongNotContainFilter(int rowCount, Set<Long> notInGroups, Column<Long> column) {
        super(rowCount, new LongContainFilter(notInGroups, column));
    }
}
