package com.fr.swift.result;

import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.queue.SortedListMergingUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-17
 */
public class SortedQueryResultSetMerger implements QueryResultSetMerger<List<Row>> {
    private static Comparator<Row> createRowComparator(final List<Pair<Sort, Comparator>> comparators) {
        Collections.sort(comparators, new Comparator<Pair<Sort, Comparator>>() {
            @Override
            public int compare(Pair<Sort, Comparator> o1, Pair<Sort, Comparator> o2) {
                return o1.getKey().getTargetIndex() - o2.getKey().getTargetIndex();
            }
        });
        return new Comparator<Row>() {
            @Override
            public int compare(Row o1, Row o2) {
                for (Pair<Sort, Comparator> pair : comparators) {
                    int result = pair.getValue().compare(o1.getValue(pair.getKey().getTargetIndex()), o2.getValue(pair.getKey().getTargetIndex()));
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }

    @Override
    public QueryResultSet<List<Row>> merge(List<? extends QueryResultSet<List<Row>>> queryResultSets) throws SQLException {
        int rowCount = 0;
        for (QueryResultSet<List<Row>> queryResultSet : queryResultSets) {
            rowCount += ((DetailQueryResultSet) queryResultSet).getRowCount();
        }
        // TODO fetch size和 comparators
        return new SortMultiSegmentDetailResultSet(
                queryResultSets.get(0).getFetchSize(), rowCount,
                new SortedRowIterator(queryResultSets.get(0).getFetchSize(),
                        createRowComparator(Collections.<Pair<Sort, Comparator>>emptyList()),
                        (List<DetailQueryResultSet>) queryResultSets));
    }

    /**
     * Created by Lyon on 2018/7/20.
     */
    static class SortedRowIterator implements Iterator<List<Row>> {

        private int pageSize;
        private List<DetailQueryResultSet> resultSets;
        private Comparator<Row> comparator;
        private List<Row> remainRows = new ArrayList<Row>(0);
        private Row[] lastRowOfPrevPage;

        SortedRowIterator(int pageSize, Comparator<Row> comparator, List<DetailQueryResultSet> resultSets) {
            this.pageSize = pageSize;
            this.comparator = comparator;
            this.resultSets = resultSets;
            this.lastRowOfPrevPage = new Row[resultSets.size()];
        }

        /**
         * remainRows.size() < pageSize情况下，从每个resultSet中取出一页进行合并
         *
         * @return
         */
        private List<Row> updateAll() {
            List<List<Row>> lists = new ArrayList<List<Row>>();
            for (int i = 0; i < resultSets.size(); i++) {
                if (resultSets.get(i).hasNextPage()) {
                    calNextPage(lists, i);
                }
            }
            if (!remainRows.isEmpty()) {
                lists.add(remainRows);
            }
            Iterator<Row> iterator = lists.isEmpty() ?
                    IteratorUtils.<Row>emptyIterator() : SortedListMergingUtils.merge(lists, comparator);
            return getPage(iterator);
        }

        /**
         * remainRows.size() >= pageSize情况下，更新步骤如下：
         * 1、从remainRows中取出第pageSize行lastRow
         * 2、判断lastRow是否<=resultSet前一页的最后一行，如果是，则不更新该resultSet，否则取这个resultSet的下一页
         * 3、合并
         *
         * @return
         */
        private List<Row> getNext() {
            if (remainRows.size() < pageSize) {
                return updateAll();
            }
            Row lastRow = remainRows.get(pageSize - 1);
            List<List<Row>> newPages = new ArrayList<List<Row>>();
            for (int i = 0; i < resultSets.size(); i++) {
                if (resultSets.get(i).hasNextPage()) {
                    if (shouldUpdate(lastRow, lastRowOfPrevPage[i])) {
                        calNextPage(newPages, i);
                    }
                }
            }
            Iterator<Row> iterator;
            if (!newPages.isEmpty()) {
                newPages.add(remainRows);
                iterator = SortedListMergingUtils.merge(newPages, comparator);
            } else {
                iterator = remainRows.iterator();
            }
            List<Row> page = getPage(iterator);
            if (page.size() < pageSize && remainRows.isEmpty() && hasNext()) {
                // 按照前面的规则更新了，但是不满一页，并且源结果集还有剩余，继续取下一页
                remainRows = page;
                return getNext();
            }
            return page;
        }

        private void calNextPage(List<List<Row>> newPages, int i) {
            List<Row> rows = resultSets.get(i).getPage();
            if (!rows.isEmpty()) {
                newPages.add(rows);
                lastRowOfPrevPage[i] = rows.get(rows.size() - 1);
            }
        }

        private boolean shouldUpdate(Row remainRow, Row lastRowOfPrevPage) {
            return comparator.compare(remainRow, lastRowOfPrevPage) > 0;
        }

        private List<Row> getPage(Iterator<Row> iterator) {
            List<Row> ret = new ArrayList<Row>();
            remainRows = new ArrayList<Row>();
            int count = pageSize;
            while (iterator.hasNext()) {
                if (count-- > 0) {
                    ret.add(iterator.next());
                } else {
                    remainRows.add(iterator.next());
                }
            }
            return ret;
        }

        @Override
        public boolean hasNext() {
            if (!remainRows.isEmpty()) {
                return true;
            }
            for (DetailQueryResultSet resultSet : resultSets) {
                if (resultSet.hasNextPage()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<Row> next() {
            return getNext();
        }

        @Override
        public void remove() {
        }
    }
}
