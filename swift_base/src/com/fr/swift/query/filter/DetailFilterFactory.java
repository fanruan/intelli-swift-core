package com.fr.swift.query.filter;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.string.StringInFilter;
import com.fr.swift.query.filter.detail.impl.string.StringLikeFilter;
import com.fr.swift.query.filter.info.SwiftDetailFilterValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;

import java.util.Set;

/**
 * Created by Lyon on 2018/2/2.
 */
public class DetailFilterFactory {

    public static DetailFilter createFilter(Segment segment, SwiftDetailFilterValue filterValue) {
        Column column = segment.getColumn(new ColumnKey(filterValue.getFieldName()));
        switch (filterValue.getType()) {
            case STRING_IN:
                return new StringInFilter((Set<String>) filterValue.getFilterValue(), column);
            case STRING_LIKE:
                return new StringLikeFilter((String) filterValue.getFilterValue(), column);
            default:
        }
        return null;
    }
}
