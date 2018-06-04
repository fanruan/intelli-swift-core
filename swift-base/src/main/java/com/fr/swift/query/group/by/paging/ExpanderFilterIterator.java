package com.fr.swift.query.group.by.paging;

import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/20.
 */
public class ExpanderFilterIterator implements Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> {

    private Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> iterator;

    public ExpanderFilterIterator(Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> iterator, Expander expander) {
        this.iterator = new FilteredIterator<KeyValue<RowIndexKey<int[]>, RowTraversal>>(iterator, new Filter<KeyValue<RowIndexKey<int[]>, RowTraversal>>() {
            @Override
            public boolean accept(KeyValue<RowIndexKey<int[]>, RowTraversal> rowIndexKeyRowTraversalKeyValue) {
                return false;
            }
        });
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public KeyValue<RowIndexKey<int[]>, RowTraversal> next() {
        return null;
    }

    @Override
    public void remove() {

    }
}
