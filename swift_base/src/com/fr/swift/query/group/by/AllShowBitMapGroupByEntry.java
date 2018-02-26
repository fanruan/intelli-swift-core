package com.fr.swift.query.group.by;

import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author pony
 * @date 2018/1/24
 */
public class AllShowBitMapGroupByEntry implements GroupByEntry {
    private int index;
    private BitmapIndexedColumn bitMapColumn;

    public AllShowBitMapGroupByEntry(int index, BitmapIndexedColumn bitMapColumn) {
        this.index = index;
        this.bitMapColumn = bitMapColumn;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public RowTraversal getTraversal() {
        return bitMapColumn.getBitMapIndex(index);
    }

}
