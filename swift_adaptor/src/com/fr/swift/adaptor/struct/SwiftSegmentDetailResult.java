package com.fr.swift.adaptor.struct;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.SwiftMetaData;

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

    public SwiftSegmentDetailResult(List<Segment> segments, SwiftMetaData swiftMetaData) throws SwiftMetaDataException {
        List<List<BIDetailCell>> dataList = new ArrayList<List<BIDetailCell>>();
        for (Segment segment : segments) {
            List<DetailColumn> columnList = new ArrayList<DetailColumn>();
            int count = segment.getRowCount();
            for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
                String columnName = swiftMetaData.getColumnName(i);
                ColumnKey columnKey = new ColumnKey(columnName);
                columnList.add(segment.getColumn(columnKey).getDetailColumn());
            }
            for (int i = 0; i < count; i++) {
                List<BIDetailCell> cellList = new ArrayList<BIDetailCell>();
                for (int j = 0; j < swiftMetaData.getColumnCount(); j++) {
                    BIDetailCell cell = new SwiftDetailCell(columnList.get(j).get(i));
                    cellList.add(cell);
                }
                dataList.add(cellList);
            }
        }
        this.dataIterator = dataList.iterator();
        this.rowCount = dataList.size();
        this.columnSize = swiftMetaData.getColumnCount();
    }

    public SwiftSegmentDetailResult(Iterator<List<BIDetailCell>> dataIterator, int rowCount, int columnSize) {
        this.dataIterator = dataIterator;
        this.rowCount = rowCount;
        this.columnSize = columnSize;
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
}
