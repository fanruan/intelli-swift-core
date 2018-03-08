package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.AbstractFilter;
import com.fr.swift.query.filter.detail.impl.util.LookupFactory;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.ArrayLookupHelper;
import com.fr.swift.util.MatchAndIndex;
import com.fr.swift.util.Util;

/**
 * Created by Lyon on 2017/11/27.
 */
public class NumberInRangeFilter extends AbstractFilter<Number> {

    protected final Double min;
    protected final Double max;
    protected final boolean minIncluded;
    protected final boolean maxIncluded;

    public NumberInRangeFilter(Double min, Double max, boolean minIncluded, boolean maxIncluded, Column<Number> column) {
        Util.requireNotGreater(min, max);
        this.min = min;
        this.max = max;
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(final DictionaryEncodedColumn<Number> dict) {
        ArrayLookupHelper.Lookup<Number> lookup = LookupFactory.create(dict);
        MatchAndIndex minMatchAndIndex = ArrayLookupHelper.binarySearch(lookup, dict.convertValue(min));
        MatchAndIndex maxMatchAndIndex = ArrayLookupHelper.binarySearch(lookup, dict.convertValue(max));
        // 获取过滤条件对应的RangeIntList区间
        int start, end;
        if (minMatchAndIndex.isMatch()) {
            if (minIncluded) {
                start = minMatchAndIndex.getIndex();
            } else {
                // min这个分组值存在但是不包含该分组值的条件下，索引值加1
                start = minMatchAndIndex.getIndex() + 1;
            }
        } else {
            // min这个分组值不存在的条件下，索引值要加1
            start = minMatchAndIndex.getIndex() + 1;
        }
        if (maxMatchAndIndex.isMatch()) {
            if (maxIncluded) {
                end = maxMatchAndIndex.getIndex();
            } else {
                // max这个分组值存在但是不包含该分组值的条件下，索引值减1
                end = maxMatchAndIndex.getIndex() - 1;
            }
        } else {
            // max这个分组值不存在的条件下，取当前索引值
            end = maxMatchAndIndex.getIndex();
        }
        start = start < 0 ? 0 : start;
        if (start >= dict.size() || end < 0 || start > end) {
            return new IntListRowTraversal(IntListFactory.createEmptyIntList());
        }
        return new IntListRowTraversal(IntListFactory.createRangeIntList(start, end));
    }

    @Override
    public boolean matches(SwiftNode node) {
        Object data = node.getData();
        if (data == null) {
            return false;
        }
        double value = ((Number) data).doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        return (minIncluded ? value >= minValue : value > minValue) &&
                (maxIncluded ? value <= maxValue : value < maxValue);
    }
}
