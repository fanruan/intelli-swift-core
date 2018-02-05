package com.fr.swift.query.filter.detail.impl.date;

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

/**
 * Created by Lyon on 2017/11/29.
 */
public class DateInRangeFilter extends AbstractFilter<Long> {

    private Long start;
    private Long end;

    /**
     * 日期过滤，包含开始和结束日期
     *
     * @param start
     * @param end
     */
    public DateInRangeFilter(Long start, Long end, Column<Long> column) {
        this.start = start;
        this.end = end;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(final DictionaryEncodedColumn<Long> dict) {
        ArrayLookupHelper.Lookup<Long> lookup = LookupFactory.create(dict);
        MatchAndIndex startMatchAndIndex = ArrayLookupHelper.binarySearch(lookup, start);
        MatchAndIndex endMatchAndIndex = ArrayLookupHelper.binarySearch(lookup, end);
        int startIndex = startMatchAndIndex.isMatch() ? startMatchAndIndex.getIndex() : startMatchAndIndex.getIndex() + 1;
        // 不管是否match，该index都是过滤结果的最后一个分组值
        int endIndex = endMatchAndIndex.getIndex();
        startIndex = startIndex < 0 ? 0 : startIndex;
        if (startIndex >= dict.size() || endIndex <= 0 || startIndex > endIndex) {
            return new IntListRowTraversal(IntListFactory.createEmptyIntList());
        }
        return new IntListRowTraversal(IntListFactory.createRangeIntList(startIndex, endIndex));
    }

    @Override
    public boolean matches(SwiftNode node) {
        Long date = (Long) node.getData();
        if (date == null){
            return false;
        }
        return largerThanStart(date) && smallerThanEnd(date);
    }

    private boolean smallerThanEnd(Long date) {
        return end == null || end>= date;
    }

    private boolean largerThanStart(Long date) {
        return start == null || date >= start;
    }
}
