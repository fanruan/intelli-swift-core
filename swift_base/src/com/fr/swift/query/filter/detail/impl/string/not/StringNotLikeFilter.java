package com.fr.swift.query.filter.detail.impl.string.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.string.StringLikeFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/27.
 */
public class StringNotLikeFilter extends AbstractNotOperatorFilter {

    public StringNotLikeFilter(String notLike, int rowCount, Column<String> column) {
        super(rowCount, new StringLikeFilter(notLike, column));
    }
}
