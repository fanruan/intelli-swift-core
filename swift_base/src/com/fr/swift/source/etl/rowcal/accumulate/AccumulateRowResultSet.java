package com.fr.swift.source.etl.rowcal.accumulate;

import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.utils.GroupValueIterator;
import com.fr.swift.source.etl.utils.SwiftValuesAndGVI;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 15:47
 */
public class AccumulateRowResultSet implements SwiftResultSet {

    private ColumnKey columnKey;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private ColumnKey[] dimensions;
    private int segCursor, rowCursor, rowCount;
    private double sum;
    private TempValue tempValue;
    private Segment[] tempSegment;
    private RowTraversal[] traversal;
    private GroupValueIterator iterator;
    private SwiftValuesAndGVI valuesAndGVI;
    private boolean needNext;

    public AccumulateRowResultSet(ColumnKey columnKey, Segment[] segments, SwiftMetaData metaData, ColumnKey[] dimensions) {
        this.columnKey = columnKey;
        this.segments = segments;
        this.metaData = metaData;
        this.dimensions = dimensions;
        init();
    }

    private void init() {
        segCursor = 0;
        rowCursor = 0;
        sum = 0;
        needNext = true;
        rowCount = segments[0].getRowCount();
        tempValue = new TempValue();
        if(dimensions != null) {
            iterator = new GroupValueIterator(segments, dimensions, null);
            tempSegment = new Segment[0];
            traversal = new RowTraversal[0];
        }
    }


    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if(dimensions != null) {
            if(iterator.hasNext() || (segCursor < tempSegment.length && rowCursor < rowCount)) {
                if(needNext) {
                    valuesAndGVI = iterator.next();
                    traversal = new RowTraversal[valuesAndGVI.getAggreator().size()];
                    tempSegment = new Segment[valuesAndGVI.getAggreator().size()];
                    for(int i = 0; i < valuesAndGVI.getAggreator().size(); i++) {
                        traversal[i] = valuesAndGVI.getAggreator().get(i).getBitMap();
                        tempSegment[i] = valuesAndGVI.getAggreator().get(i).getSegment();
                    }
                    rowCount = traversal[0].getCardinality();
                    segCursor = 0;
                    rowCursor = 0;
                    needNext = false;
                    sum = 0;
                }
                if(segCursor == tempSegment.length - 1 && rowCursor == rowCount - 1) {
                    needNext = true;
                }
                nextValueForDimension();
                return true;
            }
            return false;
        } else {
            return nextValue();
        }
    }

    private boolean nextValueForDimension() throws SQLException {
        if(segCursor < tempSegment.length && rowCursor < rowCount) {
            rowCount = traversal[segCursor].getCardinality();
            DictionaryEncodedColumn getter = tempSegment[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            final Index index = new Index();
            traversal[segCursor].breakableTraversal(new BreakTraversalAction() {
                int cursor = 0;
                @Override
                public boolean actionPerformed(int row) {
                    if(cursor == rowCursor) {
                        index.setIndex(row);
                        return true;
                    }
                    cursor ++;
                    return false;
                }
            });
            Number v = (Number) getter.getValue(getter.getIndexByRow(index.getIndex()));
            if(null != v) {
                sum += v.doubleValue();
            }
            List dataList = new ArrayList();
            dataList.add(sum);
            tempValue.setRow(new ListBasedRow(dataList));
            if(rowCursor < rowCount - 1) {
                rowCursor ++;
            } else {
                if(segCursor < segments.length) {
                    rowCursor = 0;
                    segCursor ++;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean nextValue() throws SQLException {
        if(segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            DictionaryEncodedColumn getter = segments[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            Number v = (Number) getter.getValue(getter.getIndexByRow(rowCursor));
            if(null != v) {
                sum += v.doubleValue();
            }
            List dataList = new ArrayList();
            dataList.add(sum);
            tempValue.setRow(new ListBasedRow(dataList));
            if(rowCursor < rowCount - 1) {
                rowCursor ++;
            } else {
                if(segCursor < segments.length) {
                    rowCursor = 0;
                    segCursor ++;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() throws SQLException {
        return tempValue.getRow();
    }

    private class TempValue {

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

        ListBasedRow row;
    }

    private class Index {
        private int index = 0;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
