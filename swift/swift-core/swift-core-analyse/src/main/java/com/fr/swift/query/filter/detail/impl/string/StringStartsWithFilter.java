package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.impl.AbstractDetailFilter;
import com.fr.swift.query.filter.detail.impl.util.LookupFactory;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.ArrayLookupHelper;
import com.fr.swift.util.Strings;
import com.fr.swift.util.Util;

/**
 * Created by Lyon on 2017/11/27.
 */
public class StringStartsWithFilter extends AbstractDetailFilter<String> {
    private String startsWith;

    public StringStartsWithFilter(String startsWith, Column<String> column) {
        Util.requireNonNull(Strings.isEmpty(startsWith) ? null : startsWith);
        this.startsWith = startsWith;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(final DictionaryEncodedColumn<String> dict) {
        ArrayLookupHelper.Lookup<String> lookup = LookupFactory.create(dict);
        int start = ArrayLookupHelper.getStartIndex4StartWith(lookup, startsWith);
        int end = ArrayLookupHelper.getEndIndex4StartWith(lookup, startsWith);
        start = start < 0 ? 0 : start;
        if (start >= dict.size() || end < 0) {
            return new IntListRowTraversal(IntListFactory.createEmptyIntList());
        }
        IntList intList = IntListFactory.createRangeIntList(start, end);
        return new IntListRowTraversal(intList);
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        Object data = node.getData();
        return data != null && data.toString().startsWith(startsWith);
    }
}
