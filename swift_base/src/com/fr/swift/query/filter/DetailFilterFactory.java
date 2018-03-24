package com.fr.swift.query.filter;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.FormulaFilter;
import com.fr.swift.query.filter.detail.impl.GeneralAndFilter;
import com.fr.swift.query.filter.detail.impl.GeneralOrFilter;
import com.fr.swift.query.filter.detail.impl.NotNullFilter;
import com.fr.swift.query.filter.detail.impl.NullFilter;
import com.fr.swift.query.filter.detail.impl.date.DateInRangeFilter;
import com.fr.swift.query.filter.detail.impl.date.not.DateNotInRangeFilter;
import com.fr.swift.query.filter.detail.impl.nfilter.BottomNFilter;
import com.fr.swift.query.filter.detail.impl.nfilter.TopNFilter;
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
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftDateInRangeFilterValue;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;

import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/2/2.
 */
public class DetailFilterFactory {

    public static DetailFilter createFilter(Segment segment, SwiftDetailFilterInfo filterInfo) {
        Column column = segment.getColumn(new ColumnKey(filterInfo.getFieldName()));
        final int rowCount = segment.getRowCount();
        switch (filterInfo.getType()) {
            case STRING_IN:
                return new StringInFilter((Set<String>) filterInfo.getFilterValue(), column);
            case STRING_LIKE:
                return new StringLikeFilter((String) filterInfo.getFilterValue(), column);
            case STRING_STARTS_WITH:
                return new StringStartsWithFilter((String) filterInfo.getFilterValue(), column);
            case STRING_ENDS_WITH:
                return new StringEndsWithFilter((String) filterInfo.getFilterValue(), column);
            case STRING_NOT_IN:
                return new StringNotInFilter((Set<String>) filterInfo.getFilterValue(), rowCount, column);
            case STRING_NOT_LIKE:
                return new StringNotLikeFilter((String) filterInfo.getFilterValue(), rowCount, column);
            case STRING_NOT_STARTS_WITH:
                return new StringNotStartsWithFilter((String) filterInfo.getFilterValue(), rowCount, column);
            case STRING_NOT_ENDS_WITH:
                return new StringNotEndsWithFilter((String) filterInfo.getFilterValue(), rowCount, column);

            case NUMBER_CONTAIN:
                return new NumberContainFilter((Set<Double>) filterInfo.getFilterValue(), column);
            case NUMBER_IN_RANGE:
                SwiftNumberInRangeFilterValue value = (SwiftNumberInRangeFilterValue) filterInfo.getFilterValue();
                return new NumberInRangeFilter(value.getMin(), value.getMax(),
                        value.isMinIncluded(), value.isMaxIncluded(), column);
            case NUMBER_NOT_CONTAIN:
                return new NumberNotContainFilter(rowCount, (Set<Double>) filterInfo.getFilterValue(), column);
            case NUMBER_NOT_IN_RANGE:
                SwiftNumberInRangeFilterValue value1 = (SwiftNumberInRangeFilterValue) filterInfo.getFilterValue();
                return new NumberNotInRangeFilter(rowCount, value1.getMin(), value1.getMax(), value1.isMinIncluded(),
                        value1.isMaxIncluded(), column);

            case DATE_IN_RANGE:
                SwiftDateInRangeFilterValue value2 = (SwiftDateInRangeFilterValue) filterInfo.getFilterValue();
                return new DateInRangeFilter(value2.getStart(), value2.getEnd(), column);
            case DATE_NOT_IN_RANGE:
                SwiftDateInRangeFilterValue value3 = (SwiftDateInRangeFilterValue) filterInfo.getFilterValue();
                return new DateNotInRangeFilter(rowCount, value3.getStart(), value3.getEnd(), column);
            case TOP_N:
                return new TopNFilter((Integer) filterInfo.getFilterValue(), column);
            case BOTTOM_N:
                return new BottomNFilter((Integer) filterInfo.getFilterValue(), column);
            case NULL:
                return new NullFilter(column);
            case NOT_NULL:
                return new NotNullFilter(rowCount, column);
            case AND:
                return new GeneralAndFilter((List<FilterInfo>) filterInfo.getFilterValue(), segment);
            case OR:
                return new GeneralOrFilter((List<FilterInfo>) filterInfo.getFilterValue(), segment);
            case FORMULA:
                return new FormulaFilter((String) filterInfo.getFilterValue(), segment);
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
