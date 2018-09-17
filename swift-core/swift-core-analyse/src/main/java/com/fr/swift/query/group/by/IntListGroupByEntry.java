package com.fr.swift.query.group.by;


import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author pony
 * @date 2017/12/6
 */
public class IntListGroupByEntry implements GroupByEntry {
    private int index;
    private IntListRowTraversal rowTraversal;

    public IntListGroupByEntry(int index, IntListRowTraversal rowTraversal) {
        this.index = index;
        this.rowTraversal = rowTraversal;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public RowTraversal getTraversal() {
        return rowTraversal;
    }
}
