package com.fr.swift.result;

import com.fr.swift.source.Row;

import java.util.List;

/**
 * Created by Lyon on 2018/6/19.
 */
public class SwiftRowIteratorImpl implements SwiftRowIterator {

    private DetailResultSet source;
    private List<Row> rows;
    private int cursor = 0;

    public SwiftRowIteratorImpl(DetailResultSet source) {
        this.source = source;
        this.rows = source.getPage();
    }

    @Override
    public boolean hasNext() {
        if (cursor >= rows.size() && source.hasNextPage()) {
            rows = source.getPage();
            cursor = 0;
        }
        return cursor < rows.size();
    }

    @Override
    public Row next() {
        return rows.get(cursor++);
    }

    @Override
    public void remove() {
    }
}
