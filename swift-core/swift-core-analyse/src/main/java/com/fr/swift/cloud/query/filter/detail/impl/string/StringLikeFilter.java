package com.fr.swift.cloud.query.filter.detail.impl.string;

import com.fr.swift.cloud.query.filter.detail.impl.AbstractDetailFilter;
import com.fr.swift.cloud.query.filter.match.MatchConverter;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.array.IntList;
import com.fr.swift.cloud.structure.array.IntListFactory;
import com.fr.swift.cloud.structure.iterator.IntListRowTraversal;
import com.fr.swift.cloud.structure.iterator.RowTraversal;
import com.fr.swift.cloud.util.Assert;
import com.fr.swift.cloud.util.Strings;

/**
 * Created by Lyon on 2017/11/27.
 */
public class StringLikeFilter extends AbstractDetailFilter {

    private String like;

    public StringLikeFilter(String like, Column column) {
        Assert.isFalse(Strings.isEmpty(like));
        this.like = like;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        IntList intList = IntListFactory.createIntList();
        for (int i = 0, size = dict.size(); i < size; i++) {
            Object data = dict.getValue(i);
            if (data != null && data.toString().contains(like)) {
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
