package com.fr.swift.query.filter.detail.impl.number.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.number.IntContainFilter;
import com.fr.swift.segment.column.Column;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/28.
 */
public class IntNotContainFilter extends AbstractNotOperatorFilter {

    public IntNotContainFilter(int rowCount, Set<Integer> notInGroups, Column<Integer> column) {
        super(rowCount, new IntContainFilter(notInGroups, column));
    }
}
