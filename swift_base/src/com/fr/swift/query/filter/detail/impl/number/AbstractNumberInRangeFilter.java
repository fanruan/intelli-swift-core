package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.AbstractFilter;
import com.fr.swift.query.filter.detail.impl.util.LookupFactory;
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
abstract class AbstractNumberInRangeFilter<T extends Number> extends AbstractFilter<Number> {

    protected final Number min;
    protected final Number max;
    protected final boolean minIncluded;
    protected final boolean maxIncluded;

    public AbstractNumberInRangeFilter(T min, T max, boolean minIncluded, boolean maxIncluded) {
        Util.requireNotGreater(min, max);
        this.min = min;
        this.max = max;
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;
    }

    @Override
    protected RowTraversal getIntIterator(final DictionaryEncodedColumn<Number> dict) {
        ArrayLookupHelper.Lookup<Number> lookup = LookupFactory.create(dict);
        MatchAndIndex minMatchAndIndex = ArrayLookupHelper.binarySearch(lookup, min);
        MatchAndIndex maxMatchAndIndex = ArrayLookupHelper.binarySearch(lookup, max);
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
}
