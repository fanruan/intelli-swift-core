package com.fr.swift.result;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/24
 */
public class SortMultiSegmentDetailResultSet extends AbstractDetailResultSet {

    private List<Query<DetailResultSet>> queries;
    private List<Pair<Sort, Comparator>> comparators;
    private int rowCount;
    private Iterator<List<Row>> mergerIterator;
    private Iterator<Row> rowIterator;

    public SortMultiSegmentDetailResultSet(int fetchSize, List<Query<DetailResultSet>> queries,
                                           List<Pair<Sort, Comparator>> comparators) throws SQLException {
        super(fetchSize);
        this.queries = queries;
        this.comparators = comparators;
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
        mergerIterator = resultSets.isEmpty() ? new ArrayList<List<Row>>().iterator()
                : new SortedDetailResultSetMerger(fetchSize, createRowComparator(comparators), resultSets);
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
}
