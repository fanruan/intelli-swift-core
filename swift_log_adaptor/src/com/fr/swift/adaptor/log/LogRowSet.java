package com.fr.swift.adaptor.log;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.function.Function;

import java.util.List;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogRowSet implements SwiftResultSet {
    private SwiftMetaData meta;

    private List<Object> rows;

    /**
     * fr log -> swift row
     */
    private Function<Object, Row> converter;

    private int cursor = 0;

    public LogRowSet(SwiftMetaData meta, List<Object> rows, Class<?> entity) throws Exception {
        this.meta = meta;
        this.rows = rows;
        this.converter = new SwiftRowAdaptor(entity, meta);
    }

    @Override
    public SwiftMetaData getMetaData() {
        return meta;
    }

    @Override
    public boolean next() {
        return cursor < rows.size();
    }

    @Override
    public Row getRowData() {
        return converter.apply(rows.get(cursor++));
    }

    @Override
    public void close() {
        rows = null;
    }
}