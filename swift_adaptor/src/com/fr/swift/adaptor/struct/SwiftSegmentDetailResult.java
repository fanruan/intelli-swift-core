package com.fr.swift.adaptor.struct;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cal.Query;
import com.fr.swift.cal.result.detail.NormalDetailResultQuery;
import com.fr.swift.cal.result.detail.SortDetailResultQuery;
import com.fr.swift.cal.segment.detail.NormalDetailSegmentQuery;
import com.fr.swift.cal.segment.detail.SortDetailSegmentQuery;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.IntList;

import java.sql.SQLException;
import java.util.ArrayList;
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
        for (Segment segment : segments) {
            List<Column> columnList = new ArrayList<Column>();
            rowCount += segment.getRowCount();
            for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
                String columnName = swiftMetaData.getColumnName(i);
                ColumnKey columnKey = new ColumnKey(columnName);
                columnList.add(segment.getColumn(columnKey));
            }
            queryList.add(new SortDetailSegmentQuery(columnList, new AllShowFilter(segment.getAllShowIndex()), sortIndex, sorts));
        }
        Query<DetailResultSet> query = null;
        if (queryList.size() == 1) {
            query = queryList.get(0);
        } else {
            query = new SortDetailResultQuery(queryList);
        }
        this.dataIterator = new DetailResultIterator(query.getQueryResult(), swiftMetaData);
    }

    private void initNoneSort(List<Segment> segments, SwiftMetaData swiftMetaData) throws SQLException {
        this.rowCount = 0;
        List<Query<DetailResultSet>> queryList = new ArrayList<Query<DetailResultSet>>();
        for (Segment segment : segments) {
            List<Column> columnList = new ArrayList<Column>();
            rowCount += segment.getRowCount();
            for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
                String columnName = swiftMetaData.getColumnId(i);
                ColumnKey columnKey = new ColumnKey(columnName);
                columnList.add(segment.getColumn(columnKey));
            }
            queryList.add(new NormalDetailSegmentQuery(columnList, new AllShowFilter(segment.getAllShowIndex())));
        }
        Query<DetailResultSet> query = null;
        if (queryList.size() == 1) {
            query = queryList.get(0);
        } else {
            query = new NormalDetailResultQuery(queryList);
        }
        this.dataIterator = new DetailResultIterator(query.getQueryResult(), swiftMetaData);
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
    public ResultType getResultType() {
        return ResultType.DETAIL;
    }

    private class AllShowFilter implements DetailFilter {
        private ImmutableBitMap bitMap;

        public AllShowFilter(ImmutableBitMap bitMap) {
            this.bitMap = bitMap;
        }

        @Override
        public ImmutableBitMap createFilterIndex() {
            return bitMap;
        }

        @Override
        public boolean matches(SwiftNode node) {
            return true;
        }
    }

    private class DetailResultIterator implements Iterator<List<BIDetailCell>> {
        private DetailResultSet resultSet;
        private SwiftMetaData meta;

        public DetailResultIterator(DetailResultSet resultSet, SwiftMetaData meta) {
            this.resultSet = resultSet;
            this.meta = meta;
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
                    BIDetailCell cell = new SwiftDetailCell(isDate(meta, i + 1) ?
                            new java.sql.Date(((Long) row.getValue(i))) :
                            row.getValue(i));
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

        private boolean isDate(SwiftMetaData meta, int i) throws SwiftMetaDataException {
            return ColumnType.DATE ==
                    ColumnTypeUtils.sqlTypeToColumnType(
                            meta.getColumnType(i),
                            meta.getPrecision(i),
                            meta.getScale(i));
        }
    }
}
