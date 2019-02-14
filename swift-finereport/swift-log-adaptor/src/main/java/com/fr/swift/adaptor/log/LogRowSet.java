package com.fr.swift.adaptor.log;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.function.Function;

import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogRowSet implements SwiftResultSet {

    private SwiftMetaData meta;

    private Iterator<Object> rowsItr;

    /**
     * fr log -> swift row
     */
    private Function<Object, Row> converter;

    public LogRowSet(SwiftMetaData meta, Iterable<Object> rows, Class<?> entity) throws Exception {
        this.meta = meta;
        this.rowsItr = rows.iterator();
        this.converter = new SwiftRowAdaptor(entity, meta);
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return meta;
    }

    @Override
    public boolean hasNext() {
        return rowsItr.hasNext();
    }

    @Override
    public Row getNextRow() {
        return converter.apply(rowsItr.next());
    }

    @Override
    public void close() {
        rowsItr = null;
    }
}