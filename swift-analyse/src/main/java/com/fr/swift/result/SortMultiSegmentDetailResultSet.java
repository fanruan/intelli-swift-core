package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/24
 * @author yee
 */
public class SortMultiSegmentDetailResultSet extends BaseDetailQueryResultSet {

    private int rowCount;
    private Iterator<List<Row>> mergerIterator;
    private IDetailQueryResultSetMerger merger;

    public SortMultiSegmentDetailResultSet(int fetchSize, int rowCount,
                                           SortedDetailResultSetMerger.SortedRowIterator mergerIterator,
                                           IDetailQueryResultSetMerger merger) {
        super(fetchSize);
        this.rowCount = rowCount;
        this.mergerIterator = mergerIterator;
        this.merger = merger;
    }

    @Override
    public List<Row> getPage() {
        if (mergerIterator.hasNext()) {
            return mergerIterator.next();
        }
        return new ArrayList<Row>(0);
    }

    @Override
    public boolean hasNextPage() {
        return mergerIterator.hasNext();
    }

    @Override
    public <Q extends QueryResultSet<List<Row>>> QueryResultSetMerger<List<Row>, Q> getMerger() {
        return (QueryResultSetMerger<List<Row>, Q>) merger;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public SwiftResultSet convert(final SwiftMetaData metaData) {
        return create(fetchSize, metaData, this);
    }

    static SwiftResultSet create(final int fetchSize, final SwiftMetaData metaData, final DetailQueryResultSet resultSet) {
        final Iterator<Row> iterator = new SwiftRowIteratorImpl<DetailQueryResultSet>(resultSet);
        return new DetailResultSet() {
            @Override
            public List<Row> getPage() {
                return null;
            }

            @Override
            public boolean hasNextPage() {
                return false;
            }

            @Override
            public int getRowCount() {
                return resultSet.getRowCount();
            }

            @Override
            public void setMetaData(SwiftMetaData metaData) {

            }

            @Override
            public int getFetchSize() {
                return fetchSize;
            }

            @Override
            public SwiftMetaData getMetaData() throws SQLException {
                return metaData;
            }

            @Override
            public boolean hasNext() throws SQLException {
                return iterator.hasNext();
            }

            @Override
            public Row getNextRow() throws SQLException {
                return iterator.next();
            }

            @Override
            public void close() throws SQLException {

            }
        };
    }
}
