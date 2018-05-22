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

import java.util.Set;

/**
 * Created by Lyon on 2017/11/24.
 */
public class StringInFilter extends AbstractDetailFilter<String> {

    private Set<String> groups;

    public StringInFilter(Set<String> groups, Column<String> column) {
        this.groups = groups;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn<String> dict) {
        IntList intList = IntListFactory.createIntList();
        for (String group : groups) {
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
        return data != null && (groups.contains(converter.convert(data)));
    }
}
