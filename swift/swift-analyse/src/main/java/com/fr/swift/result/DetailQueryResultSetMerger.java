package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.Row;

import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-16
 */
public class DetailQueryResultSetMerger implements IDetailQueryResultSetMerger {

    private int fetchSize;

    public DetailQueryResultSetMerger(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public DetailQueryResultSet merge(List<DetailQueryResultSet> queryResultSets) {
        int rowCount = 0;
        for (QueryResultSet<List<Row>> queryResultSet : queryResultSets) {
            rowCount += ((DetailQueryResultSet) queryResultSet).getRowCount();
        }
        return new MultiSegmentDetailResultSet(fetchSize, rowCount, new DetailRowIterator(queryResultSets), this);
    }

    static class DetailRowIterator implements Iterator<Row> {

        private int index = 0;
        private List<DetailQueryResultSet> resultSets;

        public DetailRowIterator(List<DetailQueryResultSet> resultSets) {
            this.resultSets = resultSets;
        }

        @Override
        public boolean hasNext() {
            while (index < resultSets.size()) {
                if (resultSets.get(index).hasNext()) {
                    return true;
                }
                index++;
            }
            return false;
        }

        @Override
        public Row next() {
            return resultSets.get(index).getNextRow();
        }

        @Override
        public void remove() {

        }
    }
}
