package com.fr.swift.cloud.query.group.by;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

/**
 * @author pony
 * @date 2017/12/6
 */
public class BitMapGroupByEntry implements GroupByEntry {
    private int index;
    private BitmapIndexedColumn bitMapColumn;
    private ImmutableBitMap bitmap;

    public BitMapGroupByEntry(int index, BitmapIndexedColumn bitMapColumn, ImmutableBitMap bitmap) {
        this.index = index;
        this.bitMapColumn = bitMapColumn;
        this.bitmap = bitmap;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public RowTraversal getTraversal() {
        return bitMapColumn.getBitMapIndex(index).getAnd(bitmap);
    }
}
