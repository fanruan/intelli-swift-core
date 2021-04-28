package com.fr.swift.cloud.query.filter.detail.impl.number;

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
 * @author yee
 * @date 2019-08-20
 */
public class NumberStartsWithFilter extends AbstractDetailFilter {
    private String startsWith;

    public NumberStartsWithFilter(String startsWith, Column column) {
        Assert.isFalse(Strings.isEmpty(startsWith));
        this.startsWith = startsWith;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        IntList intList = IntListFactory.createIntList();
        for (int i = 0, size = dict.size(); i < size; i++) {
            final Object value = dict.getValue(i);
            String data = null == value ? null : value.toString();
            if (data != null && data.startsWith(startsWith)) {
                intList.add(i);
            }
        }
        return new IntListRowTraversal(intList);
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        Object data = node.getData();
        return data != null && data.toString().startsWith(startsWith);
    }
}
