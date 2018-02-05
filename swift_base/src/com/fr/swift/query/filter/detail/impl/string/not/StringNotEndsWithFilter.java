package com.fr.swift.query.filter.detail.impl.string.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.string.StringEndsWithFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/27.
 */
public class StringNotEndsWithFilter extends AbstractNotOperatorFilter {

    public StringNotEndsWithFilter(String notEndsWith, int rowCount, Column<String> column) {
        super(rowCount, new StringEndsWithFilter(notEndsWith, column));
    }
}
