package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.AbstractFilter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.Util;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/28.
 */
abstract class AbstractNumberContainFilter<T extends Number> extends AbstractFilter<T> {

    private Set<T> groups;

    public AbstractNumberContainFilter(Set<T> groups) {
        Util.requireNonNull(groups);
        this.groups = groups;
    }

    protected abstract Number getNodeData(Number value);

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn<T> dict) {
        IntList intList = IntListFactory.createIntList();
        for (T number : groups) {
            intList.add(dict.getIndex(number));
        }
        return new IntListRowTraversal(intList);
    }

    @Override
    public boolean matches(SwiftNode node) {
        Object data = node.getData();
        if (data == null) {
            return false;
        }
        Number value = (Number) data;
        return groups.contains(getNodeData(value));
    }
}
