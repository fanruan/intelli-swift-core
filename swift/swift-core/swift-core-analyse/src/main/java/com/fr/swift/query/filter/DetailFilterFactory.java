package com.fr.swift.query.filter;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.query.filter.detail.impl.EmptyDetailFilter;
import com.fr.swift.query.filter.detail.impl.InFilter;
import com.fr.swift.query.filter.detail.impl.NullFilter;
import com.fr.swift.query.filter.detail.impl.nfilter.BottomNFilter;
import com.fr.swift.query.filter.detail.impl.nfilter.TopNFilter;
import com.fr.swift.query.filter.detail.impl.number.NumberAverageFilter;
import com.fr.swift.query.filter.detail.impl.number.NumberInRangeFilter;
import com.fr.swift.query.filter.detail.impl.string.StringEndsWithFilter;
import com.fr.swift.query.filter.detail.impl.string.StringKeyWordFilter;
import com.fr.swift.query.filter.detail.impl.string.StringLikeFilter;
import com.fr.swift.query.filter.detail.impl.string.StringStartsWithFilter;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.util.Optional;
import com.fr.swift.util.ReflectUtils;
import com.fr.swift.util.Strings;
import com.fr.swift.util.function.Supplier;

import java.util.Set;

/**
 * Created by Lyon on 2018/2/2.
 */
public class DetailFilterFactory {

    public static DetailFilter createFilter(Segment segment, SwiftDetailFilterInfo filterInfo) {
        // 通用过滤器没有fieldName
        int rowCount = segment == null ? 0 : segment.getRowCount();
        ColumnKey columnKey = filterInfo.getColumnKey();
        Column column;
        if (null == columnKey || Strings.isEmpty(columnKey.getName())) {
            column = null;
        } else {
            column = segment == null ? null : segment.getColumn(columnKey);
        }
        switch (filterInfo.getType()) {
            case IN:
                return new InFilter((Set<Object>) filterInfo.getFilterValue(), column);
            case STRING_LIKE:
                return new StringLikeFilter((String) filterInfo.getFilterValue(), column);
            case STRING_STARTS_WITH:
                return new StringStartsWithFilter((String) filterInfo.getFilterValue(), column);
            case STRING_ENDS_WITH:
                return new StringEndsWithFilter((String) filterInfo.getFilterValue(), column);

            case NUMBER_IN_RANGE: {
                SwiftNumberInRangeFilterValue value = (SwiftNumberInRangeFilterValue) filterInfo.getFilterValue();
                return new NumberInRangeFilter(value.getMin(), value.getMax(),
                        value.isMinIncluded(), value.isMaxIncluded(), column, rowCount);
            }
            case NUMBER_AVERAGE: {
                SwiftNumberInRangeFilterValue value = (SwiftNumberInRangeFilterValue) filterInfo.getFilterValue();
                return new NumberAverageFilter(new NumberInRangeFilter(value.getMin(), value.getMax(),
                        value.isMinIncluded(), value.isMaxIncluded(), column, rowCount));
            }
            case TOP_N:
                return new TopNFilter((Integer) filterInfo.getFilterValue(), column);
            case BOTTOM_N:
                return new BottomNFilter((Integer) filterInfo.getFilterValue(), column);
            case NULL:
                return new NullFilter(column);
            case FORMULA:
                final String className = "com.fr.swift.query.filter.FormulaFilter";
                Optional<DetailFilter> instance = ReflectUtils.newInstance(className, filterInfo.getFilterValue(), segment);
                return instance.orElseThrow(new Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new RuntimeException(String.format("new instance of %s failed", className));
                    }
                });
            case KEY_WORDS:
                return new StringKeyWordFilter((String) filterInfo.getFilterValue(), column);
            case EMPTY:
                return new EmptyDetailFilter();
            default:
                return new AllShowDetailFilter(segment);
        }
    }
}
