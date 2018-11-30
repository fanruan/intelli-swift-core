package com.fr.swift.api.rpc.impl.rs;

import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyon on 2018/11/30.
 */
public class SerializableResultSetImpl extends SerializableDetailResultSet {

    private static final long serialVersionUID = -234778438828300179L;
    private transient Iterator<Row> rowIterator;

    public SerializableResultSetImpl(String jsonString, SwiftMetaData metaData, List<Row> rows,
                                     boolean originHasNextPage, int rowCount) {
        super(jsonString, metaData, rows, originHasNextPage, rowCount);
    }

    @Override
    public List<Row> getPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNextPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (rowIterator == null) {
            rowIterator = rows.iterator();
        }
        return rowIterator.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return rowIterator.next();
    }
}
