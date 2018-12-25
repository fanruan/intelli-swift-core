package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.Row;

import java.util.ArrayList;
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
        private List<Row> currentPage;
        private int currentRow = -1;

        public DetailRowIterator(List<DetailQueryResultSet> resultSets) {
            this.resultSets = resultSets;
            this.currentPage = new ArrayList<Row>();
        }

        @Override
        public boolean hasNext() {
            while (index < resultSets.size()) {
                DetailQueryResultSet resultSet = resultSets.get(index);
                if (currentPage.isEmpty()) {
                    currentPage = resultSet.getPage();
                    currentRow = -1;
                }
                if (currentRow++ < currentPage.size()) {
                    return true;
                }
                resultSet.close();
                index++;
            }
            return false;
        }

        @Override
        public Row next() {

            return currentPage.get(currentRow);
        }

        @Override
        public void remove() {

        }
    }
}
