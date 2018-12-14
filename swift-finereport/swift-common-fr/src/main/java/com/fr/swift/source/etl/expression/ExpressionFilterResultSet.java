package com.fr.swift.source.etl.expression;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/6 0013 11:59
 */
public class ExpressionFilterResultSet implements SwiftResultSet {

    private Segment[] segments;
    private SwiftMetaData metaData;
    private ColumnKey columnKey;
    private int type;
    private FilterInfo[] filters;
    private Object[] values;
    private Object otherValue;
    private int rowCount, segCursor, rowCursor;
    private ImmutableBitMap[] otherBitmap;
    private List<List<TempIndex>> list;
    private TempValue tempValue;
    private boolean contain;

    public ExpressionFilterResultSet(ColumnKey columnKey, int type, FilterInfo[] filters,
                                     Object[] values, Object otherValue, Segment[] segments, SwiftMetaData metaData) {
        this.columnKey = columnKey;
        this.type = type;
        this.filters = filters;
        this.values = values;
        this.otherValue = otherValue;
        this.metaData = metaData;
        this.segments = segments;
        init();
    }

    private void init() {
        list = new ArrayList<List<TempIndex>>();
        otherBitmap = new ImmutableBitMap[segments.length];
        tempValue = new TempValue();
        contain = false;
        for (int i = 0; i < segments.length; i++) {
            otherBitmap[i] = segments[i].getAllShowIndex();
        }
        for (int i = 0; i < filters.length; i++) {
            List<TempIndex> indexList = new ArrayList<TempIndex>();
            for (int j = 0; j < segments.length; j++) {
                RowTraversal rowTraversal = filters[i].createDetailFilter(segments[j]).createFilterIndex();
                if (rowTraversal.getCardinality() != 0) {
                    otherBitmap[j].getAndNot((ImmutableBitMap) rowTraversal);
                    TempIndex tempIndex = new TempIndex();
                    tempIndex.setSegIndex(j);
                    tempIndex.setRowTraversal(rowTraversal);
                    indexList.add(tempIndex);
                } else {
                    indexList.add(null);
                }
            }
            list.add(indexList);
        }
        segCursor = 0;
        rowCount = segments[0].getRowCount();
        rowCursor = 0;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean hasNext() throws SQLException {
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            List dataList = new ArrayList();
            if (otherBitmap[segCursor].contains(rowCursor)) {
                contain = true;
                dataList.add(otherValue);
            }
            if (!contain) {
                for (int i = 0; i < filters.length; i++) {
                    List<TempIndex> indexList = list.get(i);
                    TempIndex tempIndex = indexList.get(segCursor);
                    if (tempIndex != null) {
                        ImmutableBitMap bitMap = tempIndex.getRowTraversal().toBitMap();
                        if (bitMap.contains(rowCursor)) {
                            dataList.add(values[i]);
                            break;
                        }
                    }
                }
            }
            contain = false;
            tempValue.setRow(new ListBasedRow(dataList));
            if (rowCursor < rowCount - 1) {
                rowCursor++;
            } else {
                if (segCursor < segments.length) {
                    segCursor++;
                    rowCursor = 0;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return tempValue.getRow();
    }

    private class TempValue {
        ListBasedRow row;

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

    }

    private class TempIndex {
        private int segIndex;
        private RowTraversal rowTraversal;

        public int getSegIndex() {
            return segIndex;
        }

        public void setSegIndex(int segIndex) {
            this.segIndex = segIndex;
        }

        public RowTraversal getRowTraversal() {
            return rowTraversal;
        }

        public void setRowTraversal(RowTraversal rowTraversal) {
            this.rowTraversal = rowTraversal;
        }

    }
}
