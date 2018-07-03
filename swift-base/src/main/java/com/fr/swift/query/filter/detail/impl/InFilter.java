package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Set;

/**
 * Created by Lyon on 2018/7/2.
 */
public class InFilter extends AbstractDetailFilter<Set<Object>> {

    private Set<Object> groupValues;

    public InFilter(Set<Object> groupValues, Column column) {
        this.groupValues = groupValues;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn<Set<Object>> dict) {
        IntList intList = IntListFactory.createIntList();
        for (Object group : groupValues) {
            int index = dict.getIndex(group);
            if (index != -1) {
                intList.add(index);
            }
        }
        return new IntListRowTraversal(intList);
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        Object data = node.getData();
        return data != null && (groupValues.contains(data));
    }
}
