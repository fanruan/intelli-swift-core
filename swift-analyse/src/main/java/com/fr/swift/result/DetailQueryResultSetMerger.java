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

    private static final long serialVersionUID = -324445209089300987L;
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
        private Iterator<Row> iterator;

        public DetailRowIterator(List<DetailQueryResultSet> resultSets) {
            this.resultSets = resultSets;
            this.iterator = new ArrayList<Row>().iterator();
        }

        @Override
        public boolean hasNext() {
            if (iterator.hasNext()) {
                return true;
            }
            while (index < resultSets.size()) {
                DetailQueryResultSet resultSet = resultSets.get(index);
                if (resultSet.hasNextPage()) {
                    List<Row> page = resultSet.getPage();
                    if (page != null && !page.isEmpty()) {
                        iterator = page.iterator();
                        break;
                    }
                }
                index++;
            }
            return iterator.hasNext();
        }

        @Override
        public Row next() {
            return iterator.next();
        }

        @Override
        public void remove() {

        }
    }
}
