package com.fr.swift.result;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.query.Query;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/23
 */

public class MultiSegmentDetailResultSet extends AbstractDetailResultSet {

    private int rowCount;
    private List<Query<DetailResultSet>> queries;
    /**
     * mergeIterator和rowIterator看似相同，其实不然，前者可以理解为内部实现(处理翻页缓存等)，后者为外部实现(对应SwiftResult)
     */
    private Iterator<Row> mergeIterator;
    private Iterator<Row> rowIterator;

    public MultiSegmentDetailResultSet(int fetchSize, List<Query<DetailResultSet>> queries) throws SQLException {
        super(fetchSize);
        this.queries = queries;
        init();
    }

    private void init() throws SQLException {
        List<DetailResultSet> resultSets = new ArrayList<DetailResultSet>();
        for (Query query : queries) {
            DetailResultSet resultSet = null;
            try {
                resultSet = (DetailResultSet) query.getQueryResult();
            } catch (Exception e) {
                SwiftLoggers.getLogger().info("segment query error: ", query.toString());
            }
            if (resultSet != null) {
                rowCount += resultSet.getRowCount();
                resultSets.add(resultSet);
            }
        }
        mergeIterator = new DetailMergerIterator(resultSets);
    }

    @Override
    public List<Row> getPage() {
        List<Row> rows = new ArrayList<Row>();
        int count = fetchSize;
        while (mergeIterator.hasNext() && count-- > 0) {
            rows.add(mergeIterator.next());
        }
        return rows;
    }

    @Override
    public boolean hasNextPage() {
        return mergeIterator.hasNext();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean hasNext() {
        if (rowIterator == null) {
            rowIterator = new SwiftRowIteratorImpl(this);
        }
        return rowIterator.hasNext();
    }

    @Override
    public Row getNextRow() {
        return rowIterator.next();
    }

    @Override
    public void close() {
    }

    private static class DetailMergerIterator implements Iterator<Row> {

        private int index = 0;
        private List<DetailResultSet> resultSets;

        public DetailMergerIterator(List<DetailResultSet> resultSets) {
            this.resultSets = resultSets;
        }

        @Override
        public boolean hasNext() {
            while (index < resultSets.size()) {
                try {
                    if (resultSets.get(index).hasNext()) {
                        return true;
                    }
                } catch (SQLException e) {
                    Crasher.crash(e);
                }
                index++;
            }
            return false;
        }

        @Override
        public Row next() {
            try {
                return resultSets.get(index).getNextRow();
            } catch (SQLException e) {
                Crasher.crash(e);
            }
            return null;
        }

        @Override
        public void remove() {

        }
    }
}
