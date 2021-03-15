package com.fr.swift.cloud.query.filter.detail.impl;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.impl.FasterAggregation;
import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2017/11/24.
 */
public abstract class AbstractDetailFilter<T> implements DetailFilter {

    protected Column<T> column;

    protected abstract RowTraversal getIntIterator(DictionaryEncodedColumn<T> dict);

    @Override
    public ImmutableBitMap createFilterIndex() {
        final BitmapIndexedColumn bitmapIndexedColumn = column.getBitmapIndex();
        final List<ImmutableBitMap> bitmaps = new ArrayList<ImmutableBitMap>();
        getIntIterator(column.getDictionaryEncodedColumn()).traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                bitmaps.add(bitmapIndexedColumn.getBitMapIndex(row));
            }
        });
        return FasterAggregation.or(bitmaps);
    }
}
