package com.fr.swift.query.filter.detail.impl.string.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.string.StringStartsWithFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/27.
 */
public class StringNotStartsWithFilter extends AbstractNotOperatorFilter {

    public StringNotStartsWithFilter(String notStartsWith, int rowCount, Column<String> column) {
        super(rowCount, new StringStartsWithFilter(notStartsWith, column));
    }
}
