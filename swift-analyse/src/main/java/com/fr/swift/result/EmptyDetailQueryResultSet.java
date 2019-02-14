package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * This class created on 2018/12/28
 *
 * @author Lucifer
 * @description
 */
public class EmptyDetailQueryResultSet implements DetailQueryResultSet {

    @Override
    public List<Row> getPage() {
        return null;
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }


    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        return EmptyResultSet.INSTANCE;
    }

    @Override
    public void close() {

    }

    @Override
    public QueryResultSetMerger getMerger() {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }
}