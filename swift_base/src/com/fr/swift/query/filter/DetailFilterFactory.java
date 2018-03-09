package com.fr.swift.query.filter;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.date.DateInRangeFilter;
import com.fr.swift.query.filter.detail.impl.date.not.DateNotInRangeFilter;
import com.fr.swift.query.filter.detail.impl.number.NumberContainFilter;
import com.fr.swift.query.filter.detail.impl.number.NumberInRangeFilter;
import com.fr.swift.query.filter.detail.impl.number.not.NumberNotContainFilter;
import com.fr.swift.query.filter.detail.impl.number.not.NumberNotInRangeFilter;
import com.fr.swift.query.filter.detail.impl.string.StringEndsWithFilter;
import com.fr.swift.query.filter.detail.impl.string.StringInFilter;
import com.fr.swift.query.filter.detail.impl.string.StringLikeFilter;
import com.fr.swift.query.filter.detail.impl.string.StringStartsWithFilter;
import com.fr.swift.query.filter.detail.impl.string.not.StringNotEndsWithFilter;
import com.fr.swift.query.filter.detail.impl.string.not.StringNotInFilter;
import com.fr.swift.query.filter.detail.impl.string.not.StringNotLikeFilter;
import com.fr.swift.query.filter.detail.impl.string.not.StringNotStartsWithFilter;
import com.fr.swift.query.filter.info.SwiftDetailFilterValue;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.result.SwiftNode;
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
        final int rowCount = segment.getRowCount();
        switch (filterValue.getType()) {
            case STRING_IN:
                return new StringInFilter((Set<String>) filterValue.getFilterValue(), column);
            case STRING_LIKE:
                return new StringLikeFilter((String) filterValue.getFilterValue(), column);
            case STRING_STARTS_WITH:
                return new StringStartsWithFilter((String) filterValue.getFilterValue(), column);
            case STRING_ENDS_WITH:
                return new StringEndsWithFilter((String) filterValue.getFilterValue(), column);
            case STRING_NOT_IN:
                return new StringNotInFilter((Set<String>) filterValue.getFilterValue(), rowCount, column);
            case STRING_NOT_LIKE:
                return new StringNotLikeFilter((String) filterValue.getFilterValue(), rowCount, column);
            case STRING_NOT_STARTS_WITH:
                return new StringNotStartsWithFilter((String) filterValue.getFilterValue(), rowCount, column);
            case STRING_NOT_ENDS_WITH:
                return new StringNotEndsWithFilter((String) filterValue.getFilterValue(), rowCount, column);

            case NUMBER_CONTAIN:
                return new NumberContainFilter((Set<Double>) filterValue.getFilterValue(), column);
            case NUMBER_IN_RANGE:
                SwiftNumberInRangeFilterValue value = (SwiftNumberInRangeFilterValue) filterValue.getFilterValue();
                return new NumberInRangeFilter(value.getMin(), value.getMax(),
                        value.isMinIncluded(), value.isMaxIncluded(), column);
            case NUMBER_NOT_CONTAIN:
                return new NumberNotContainFilter(rowCount, (Set<Double>) filterValue.getFilterValue(), column);
            case NUMBER_NOT_IN_RANGE:
                SwiftNumberInRangeFilterValue value1 = (SwiftNumberInRangeFilterValue) filterValue.getFilterValue();
                return new NumberNotInRangeFilter(rowCount, value1.getMin(), value1.getMax(), value1.isMinIncluded(),
                        value1.isMaxIncluded(), column);

            case DATE_IN_RANGE:
                SwiftDateInRangeFilterValue value2 = (SwiftDateInRangeFilterValue) filterValue.getFilterValue();
                return new DateInRangeFilter(value2.getStart(), value2.getEnd(), column);
            case DATE_NOT_IN_RANGE:
                SwiftDateInRangeFilterValue value3 = (SwiftDateInRangeFilterValue) filterValue.getFilterValue();
                return new DateNotInRangeFilter(rowCount, value3.getStart(), value3.getEnd(), column);
            default:
                return new DetailFilter() {
                    @Override
                    public ImmutableBitMap createFilterIndex() {
                        return BitMaps.newAllShowBitMap(rowCount);
                    }

                    @Override
                    public boolean matches(SwiftNode node) {
                        return true;
                    }
                };
        }
    }
}
