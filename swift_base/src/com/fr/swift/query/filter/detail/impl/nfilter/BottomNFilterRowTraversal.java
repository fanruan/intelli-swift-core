package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/1/2.
 */
public class BottomNFilterRowTraversal implements RowTraversal {
    private int endIndex;
    private DictionaryEncodedColumn column;

    protected BottomNFilterRowTraversal(int endIndex, DictionaryEncodedColumn column) {
        this.endIndex = endIndex;
        this.column = column;
    }

    @Override
    public void traversal(TraversalAction action) {
        for (int i = column.size() - 1; i > 0; i--) {
            int globalIndex = column.getGlobalIndexByIndex(i);
            if (globalIndex > endIndex) {
                action.actionPerformed(i);
            } else {
                i = 0;
            }
        }
    }

    @Override
    public boolean breakableTraversal(BreakTraversalAction action) {
        return false;
    }

    @Override
    public int getCardinality() {
        return 0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ImmutableBitMap toBitMap() {
        return null;
    }
}
