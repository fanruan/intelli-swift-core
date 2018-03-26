package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
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
 * Created by Handsome on 2018/2/24 0024 16:11
 */
public class AllDataRowCalculatorResultSet implements SwiftResultSet {

    private String columnName;
    private Segment[] segments;
    private ColumnKey[] dimensions;
    private SwiftMetaData metaData;
    private RowTraversal[] traversal;
    private double value;
    private AllDataCalculator cal;
    private int segCursor;
    private int rowCursor;
    private int rowCount;
    private TempValue tempValue;
    private GroupValueIterator iterator;
    private Segment[] tempSegment;
    private RowTraversal[] tempTraversal;
    private SwiftValuesAndGVI valuesAndGVI;
    private boolean needNext = true;

    public AllDataRowCalculatorResultSet(String columnName, Segment[] segments, RowTraversal[] traversal,
                                         SwiftMetaData metaData, AllDataCalculator cal, ColumnKey[] dimensions) {
        this.columnName = columnName;
        this.segments = segments;
        this.metaData = metaData;
        this.cal = cal;
        this.dimensions = dimensions;
        this.traversal = traversal;
        init();
    }

    private void init() {
        segCursor = 0;
        rowCursor = 0;
        rowCount = segments[0].getRowCount();
        tempValue = new TempValue();
        if(dimensions != null) {
            iterator = new GroupValueIterator(segments, dimensions, null);
            tempSegment = new Segment[0];
            tempTraversal = new RowTraversal[0];
        } else {
            this.value = cal.get(segments, traversal, new ColumnKey(columnName));
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
                    do {
                        valuesAndGVI = iterator.next();
                    } while (valuesAndGVI.getAggreator().isEmpty());
                    tempTraversal = new RowTraversal[valuesAndGVI.getAggreator().size()];
                    tempSegment = new Segment[valuesAndGVI.getAggreator().size()];
                    for (int i = 0; i < valuesAndGVI.getAggreator().size(); i++) {
                        tempTraversal[i] = valuesAndGVI.getAggreator().get(i).getBitMap();
                        tempSegment[i] = valuesAndGVI.getAggreator().get(i).getSegment();
                    }
                    value = cal.get(tempSegment, tempTraversal, new ColumnKey(columnName));
                    rowCount = tempTraversal[0].getCardinality();
                    segCursor = 0;
                    rowCursor = 0;
                    needNext = false;
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
            rowCount = tempTraversal[segCursor].getCardinality();
            List list = new ArrayList();
            list.add(value);
            tempValue.setRow(new ListBasedRow(list));
            if(rowCursor < rowCount - 1) {
                rowCursor ++;
            } else {
                if(segCursor < segments.length) {
                    segCursor ++;
                    rowCursor = 0;
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
            List list = new ArrayList();
            list.add(value);
            tempValue.setRow(new ListBasedRow(list));
            if(rowCursor < rowCount - 1) {
                rowCursor ++;
            } else {
                if(segCursor < segments.length) {
                    segCursor ++;
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
}
