package com.fr.swift.adaptor.struct;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.detail.NormalDetailResultQuery;
import com.fr.swift.query.segment.detail.NormalDetailSegmentQuery;
import com.fr.swift.query.segment.detail.SortDetailSegmentQuery;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.array.IntList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2018-1-16 17:02:20
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftSegmentDetailResult implements BIDetailTableResult {
    private Iterator<List<BIDetailCell>> dataIterator;
    private int rowCount;
    private int columnSize;

    public SwiftSegmentDetailResult(List<Segment> segments, SwiftMetaData swiftMetaData) throws SQLException {
        this(segments, swiftMetaData, null, null);
    }


    public SwiftSegmentDetailResult(List<Segment> segments, SwiftMetaData swiftMetaData, IntList sortIndex, List<SortType> sorts) throws SQLException {
        initData(segments, swiftMetaData, sortIndex, sorts);
        this.columnSize = swiftMetaData.getColumnCount();
    }

    protected void initData(List<Segment> segments, SwiftMetaData swiftMetaData, IntList sortIndex, List<SortType> sorts) throws SQLException {
        if (sorts == null || sorts.isEmpty()) {
            initNoneSort(segments, swiftMetaData);
        } else {
            initSort(segments, swiftMetaData, sortIndex, sorts);
        }
    }

    private void initSort(List<Segment> segments, SwiftMetaData swiftMetaData, IntList sortIndex, List<SortType> sorts) throws SQLException {
        this.rowCount = 0;
        List<Query<DetailResultSet>> queryList = new ArrayList<Query<DetailResultSet>>();
        Comparator comparator = null;
        for (Segment segment : segments) {
            List<Column> columnList = new ArrayList<Column>();
            rowCount += segment.getRowCount();
            for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
                String columnName = swiftMetaData.getColumnName(i);
                ColumnKey columnKey = new ColumnKey(columnName);
                columnList.add(segment.getColumn(columnKey));
            }
            if (comparator == null) {
                comparator = new DetailSortComparator(columnList, sortIndex, sorts);
            }
            queryList.add(new SortDetailSegmentQuery(columnList, new AllShowDetailFilter(segment), null, swiftMetaData));
        }
        Query<DetailResultSet> query = null;
        if (queryList.size() == 1) {
            query = queryList.get(0);
        } else {
//            query = new SortDetailResultQuery(queryList, comparator, swiftMetaData);
        }
        SwiftResultSet resultSet = ShowResultSet.of(query.getQueryResult(), swiftMetaData);
        dataIterator = new DetailResultIterator(resultSet);
    }

    private void initNoneSort(List<Segment> segments, SwiftMetaData swiftMetaData) throws SQLException {
        this.rowCount = 0;
        List<Query<DetailResultSet>> queryList = new ArrayList<Query<DetailResultSet>>();
        for (Segment segment : segments) {
            List<Column> columnList = new ArrayList<Column>();
            rowCount += segment.getRowCount();
            for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
                String columnName = swiftMetaData.getColumnName(i);
                ColumnKey columnKey = new ColumnKey(columnName);
                columnList.add(segment.getColumn(columnKey));
            }
            queryList.add(new NormalDetailSegmentQuery(columnList, new AllShowDetailFilter(segment), swiftMetaData));
        }
        Query<DetailResultSet> query = null;
        if (queryList.size() == 1) {
            query = queryList.get(0);
        } else {
            query = new NormalDetailResultQuery(queryList, swiftMetaData);
        }
        SwiftResultSet resultSet = ShowResultSet.of(query.getQueryResult(), swiftMetaData);
        dataIterator = new DetailResultIterator(resultSet);
    }

    @Override
    public boolean hasNext() {
        return dataIterator.hasNext();
    }

    @Override
    public List<BIDetailCell> next() {
        return dataIterator.next();
    }

    @Override
    public void remove() {

    }

    @Override
    public int rowSize() {
        return rowCount;
    }

    @Override
    public int columnSize() {
        return columnSize;
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }

    @Override
    public boolean hasPreviousPage() {
        return false;
    }

    @Override
    public int totalRowSize() {
        return rowCount;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.DETAIL;
    }

    private class DetailResultIterator implements Iterator<List<BIDetailCell>> {
        private SwiftResultSet resultSet;

        public DetailResultIterator(SwiftResultSet resultSet) {
            this.resultSet = resultSet;
        }

        @Override
        public boolean hasNext() {
            try {
                return resultSet.next();
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        public List<BIDetailCell> next() {
            List<BIDetailCell> cellList = new ArrayList<BIDetailCell>();
            try {
                Row row = resultSet.getRowData();
                for (int i = 0; i < row.getSize(); i++) {
                    BIDetailCell cell = new SwiftDetailCell(row.getValue(i));
                    cellList.add(cell);
                }
            } catch (SQLException ignore) {

            }
            return cellList;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected class DetailSortComparator implements Comparator<Row> {

        private List<Column> columns;
        private IntList sortIndex;
        private List<SortType> sorts;

        public DetailSortComparator(List<Column> columnList, IntList sortIndex, List<SortType> sorts) {
            this.columns = columnList;
            this.sortIndex = sortIndex;
            this.sorts = sorts;
        }

        @Override
        public int compare(Row o1, Row o2) {

            for (int i = 0; i < sortIndex.size(); i++) {
                int c = 0;
                //比较的列先后顺序
                int realColumn = sortIndex.get(i);
                if (sorts.get(i) == SortType.ASC) {
                    c = columns.get(realColumn).getDictionaryEncodedColumn().getComparator().compare(o1.getValue(realColumn), o2.getValue(realColumn));
                }
                if (sorts.get(i) == SortType.DESC) {
                    c = Comparators.reverse(columns.get(realColumn).getDictionaryEncodedColumn().getComparator()).compare(o1.getValue(realColumn), o2.getValue(realColumn));
                }
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
