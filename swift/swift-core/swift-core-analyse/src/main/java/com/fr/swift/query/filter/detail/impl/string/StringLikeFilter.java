package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.impl.AbstractDetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.Strings;
import com.fr.swift.util.Util;

/**
 * Created by Lyon on 2017/11/27.
 */
public class StringLikeFilter extends AbstractDetailFilter<String> {

    private String like;

    public StringLikeFilter(String like, Column<String> column) {
        Util.requireNonNull(Strings.isEmpty(like) ? null : like);
        this.like = like;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn<String> dict) {
        IntList intList = IntListFactory.createIntList();
        for (int i = 0, size = dict.size(); i < size; i++) {
            String data = dict.getValue(i);
            if (data != null && data.contains(like)) {
                intList.add(i);
            }
        }
        return new IntListRowTraversal(intList);
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        Object data = node.getData();
        return data != null && data.toString().contains(like);
    }
}
