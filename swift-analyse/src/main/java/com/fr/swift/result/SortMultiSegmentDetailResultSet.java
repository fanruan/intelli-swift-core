package com.fr.swift.result;

import com.fr.swift.query.query.Query;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.queue.SortedListMergingUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/24
 */
public class SortMultiSegmentDetailResultSet implements DetailResultSet {

    private List<Query<DetailResultSet>> queries;
    private List<Pair<Sort, Comparator>> comparators;
    private SwiftMetaData metaData;
    private int rowCount;
    private Iterator<List<Row>> mergerIterator;
    private Iterator<Row> rowIterator;

    public SortMultiSegmentDetailResultSet(List<Query<DetailResultSet>> queries,
                                           List<Pair<Sort, Comparator>> comparators, SwiftMetaData metaData) throws SQLException {
        this.queries = queries;
        this.comparators = comparators;
        this.metaData = metaData;
        init();
    }

    private void init() throws SQLException {
        List<DetailResultSet> resultSets = new ArrayList<DetailResultSet>();
        for (Query query : queries) {
            DetailResultSet resultSet = (DetailResultSet) query.getQueryResult();
            rowCount += resultSet.getRowCount();
            resultSets.add(resultSet);
        }
        mergerIterator = new SortedDetailMergerIterator(PAGE_SIZE, createRowComparator(comparators), resultSets);
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
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean next() {
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
                    // 从DictionaryColumn中取出来的比较器默认都是升序的
                    result = pair.getKey().getSortType() == SortType.ASC ? result : -result;
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }

    private static class SortedDetailMergerIterator implements Iterator<List<Row>> {

        private int pageSize;
        private List<DetailResultSet> resultSets;
        private Comparator<Row> comparator;
        private List<Row> remainRows = new ArrayList<Row>(0);
        private List<Row> next;

        public SortedDetailMergerIterator(int pageSize, Comparator<Row> comparator, List<DetailResultSet> resultSets) {
            this.pageSize = pageSize;
            this.comparator = comparator;
            this.resultSets = resultSets;
            this.next = getNext();
        }

        /**
         * 假设有个n个resultSet中取出m页数据(m <= n，有的resultSet可能取完了)和remainRows一起进行堆排序，
         * 从排序结果中取出前pageSize行，剩余行放到remainRows中用于下次合并
         *
         * @return
         */
        private List<Row> getNext() {
            List<List<Row>> lists = new ArrayList<List<Row>>();
            lists.add(remainRows);
            for (DetailResultSet resultSet : resultSets) {
                if (resultSet.hasNextPage()) {
                    lists.add(resultSet.getPage());
                }
            }
            if (!remainRows.isEmpty() && lists.size() == 1) {
                // resultSets中的数据都取出来了，只要取出remainRows中的数据返回即可
                return getPage(remainRows.iterator());
            }
            Iterator<Row> iterator = SortedListMergingUtils.merge(lists, comparator);
            return getPage(iterator);
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
            return !next.isEmpty();
        }

        @Override
        public List<Row> next() {
            List<Row> ret = next;
            next = getNext();
            return ret;
        }

        @Override
        public void remove() {

        }
    }
}
