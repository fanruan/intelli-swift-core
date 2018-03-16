package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.detail.impl.AbstractFilter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
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
public class NumberContainFilter extends AbstractFilter<Number> {

    private Set<Double> groups;

    public NumberContainFilter(Set<Double> groups, Column<Number> column) {
        Util.requireNonNull(groups);
        this.groups = groups;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn<Number> dict) {
        IntList intList = IntListFactory.createIntList();
        for (Number number : groups) {
            int index = dict.getIndex(number);
            if (index != -1) {
                intList.add(index);
            }
        }
        return new IntListRowTraversal(intList);
    }

    @Override
    public boolean matches(SwiftNode node) {
        Object data = node.getData();
        if (data == null) {
            return false;
        }
        return groups.contains(((Number) data).doubleValue());
    }
}
