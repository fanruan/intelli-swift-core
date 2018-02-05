package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Lyon on 2017/11/24.
 */
public abstract class AbstractFilter<T> implements DetailFilter {

    protected Column<T> column;

    protected abstract RowTraversal getIntIterator(DictionaryEncodedColumn<T> dict);

    @Override
    public ImmutableBitMap createFilterIndex() {
        final BitmapIndexedColumn bitmapIndexedColumn = column.getBitmapIndex();
        final BitMapOrHelper bitMapOrHelper = new BitMapOrHelper();
        getIntIterator(column.getDictionaryEncodedColumn()).traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                bitMapOrHelper.add(bitmapIndexedColumn.getBitMapIndex(row));
            }
        });
        return bitMapOrHelper.compute();
    }
}
