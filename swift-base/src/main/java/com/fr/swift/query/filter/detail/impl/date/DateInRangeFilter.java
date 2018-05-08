package com.fr.swift.query.filter.detail.impl.date;

import com.fr.swift.query.filter.detail.impl.AbstractDetailFilter;
import com.fr.swift.query.filter.detail.impl.util.LookupFactory;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.ArrayLookupHelper;
import com.fr.swift.util.MatchAndIndex;

/**
 * Created by Lyon on 2017/11/29.
 */
public class DateInRangeFilter extends AbstractDetailFilter<Long> {

    private Long startIncluded;
    private Long endIncluded;

    /**
     * 日期过滤，包含开始和结束日期
     *
     * @param startIncluded
     * @param endIncluded
     */
    public DateInRangeFilter(Long startIncluded, Long endIncluded, Column<Long> column) {
        this.startIncluded = startIncluded;
        this.endIncluded = endIncluded;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(final DictionaryEncodedColumn<Long> dict) {
        ArrayLookupHelper.Lookup<Long> lookup = LookupFactory.create(dict);
        MatchAndIndex startMatchAndIndex = ArrayLookupHelper.binarySearch(lookup, startIncluded);
        MatchAndIndex endMatchAndIndex = ArrayLookupHelper.binarySearch(lookup, endIncluded);
        int startIndex = startMatchAndIndex.isMatch() ? startMatchAndIndex.getIndex() : startMatchAndIndex.getIndex() + 1;
        // 不管是否match，该index都是过滤结果的最后一个分组值
        int endIndex = endMatchAndIndex.getIndex();
        startIndex = startIndex < 0 ? 0 : startIndex;
        if (startIndex >= dict.size() || endIndex < 0 || startIndex > endIndex) {
            return new IntListRowTraversal(IntListFactory.createEmptyIntList());
        }
        return new IntListRowTraversal(IntListFactory.createRangeIntList(startIndex, endIndex));
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex) {
        Long date = (Long) node.getData();
        if (date == null){
            return false;
        }
        return largerThanStart(date) && smallerThanEnd(date);
    }

    private boolean smallerThanEnd(Long date) {
        return endIncluded == null || endIncluded >= date;
    }

    private boolean largerThanStart(Long date) {
        return startIncluded == null || date >= startIncluded;
    }
}
