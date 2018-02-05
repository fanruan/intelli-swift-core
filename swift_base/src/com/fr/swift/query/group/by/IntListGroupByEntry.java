package com.fr.swift.query.group.by;

import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2017/12/6.
 */
public class IntListGroupByEntry implements GroupByEntry{
    private int index;
    private IntListRowTraversal rowTraversal;

    public IntListGroupByEntry(int index, IntListRowTraversal rowTraversal) {
        this.index = index;
        this.rowTraversal = rowTraversal;
    }

    public int getIndex() {
        return index;
    }

    public RowTraversal getTraversal() {
        return rowTraversal;
    }
}
