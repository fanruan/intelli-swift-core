package com.fr.swift.result;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyon on 2018/12/30.
 */
public class DetailQueryResultSetMergerTest extends TestCase {

    public void testMerge() {
        int fetchSize = 5;
        int numberOfPage = 3;
        int pageSize = 10;
        List<List<Row>> pages = new ArrayList<List<Row>>();
        for (int i = 0; i < numberOfPage; i++) {
            List<Row> page = new ArrayList<Row>();
            for (int j = 0; j < pageSize; j++) {
                List row = Collections.singletonList(0);
                page.add(new ListBasedRow(row));
            }
            pages.add(page);
        }
        int rowCount = numberOfPage * pageSize;
        int numberOfQRS = 3;
        List<DetailQueryResultSet> resultSets = new ArrayList<DetailQueryResultSet>();
        for (int i = 0; i < numberOfQRS; i++) {
            resultSets.add(new TestDetailQRS(pageSize, rowCount, pages));
        }
        DetailQueryResultSetMerger merger = new DetailQueryResultSetMerger(fetchSize);
        DetailQueryResultSet resultSet = merger.merge(resultSets);
        assertEquals(numberOfQRS * rowCount, resultSet.getRowCount());
        int count = 0;
        while (resultSet.hasNextPage()) {
            count += resultSet.getPage().size();
        }
        int totalRowCount = numberOfQRS * rowCount;
        assertEquals(totalRowCount, count);
    }

    static class TestDetailQRS extends BaseDetailQueryResultSet {

        private int rowCount;
        private Iterator<List<Row>> iterator;

        public TestDetailQRS(int fetchSize, int rowCount, List<List<Row>> pages) {
            super(fetchSize);
            this.rowCount = rowCount;
            this.iterator = pages.iterator();
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public List<Row> getPage() {
            return iterator.next();
        }

        @Override
        public boolean hasNextPage() {
            return iterator.hasNext();
        }
    }
}