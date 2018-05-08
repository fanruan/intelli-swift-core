package com.fr.swift.query.filter.detail.impl.string.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.string.StringInFilter;
import com.fr.swift.segment.column.Column;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/27.
 */
public class StringNotInFilter extends AbstractNotOperatorFilter {

    public StringNotInFilter(Set<String> notInGroups, int rowCount, Column<String> column) {
        super(rowCount, new StringInFilter(notInGroups, column));
    }
}
