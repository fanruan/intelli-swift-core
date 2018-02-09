package com.fr.swift.adaptor.struct;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2018-1-29 13:45:41
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftCombineDetailResult implements BIDetailTableResult {

    private List<List<BIDetailCell>> rowDataLists;
    private int rowCount;
    private int columnSize;
    private Iterator<List<BIDetailCell>> dataIterator;

    public SwiftCombineDetailResult(List<List<BIDetailCell>> columnDataLists, int rowCount) {
        rowDataLists = new ArrayList<List<BIDetailCell>>();
        this.rowCount = rowCount;
        this.columnSize = columnDataLists.size();
        column2Row(columnDataLists, rowDataLists, rowCount);
        dataIterator = rowDataLists.iterator();
    }

    private void column2Row(List<List<BIDetailCell>> columnDataLists, List<List<BIDetailCell>> rowDataLists, int rowCount) {
        for (int i = 0; i < rowCount; i++) {
            List<BIDetailCell> rowData = new ArrayList<BIDetailCell>();
            for (List<BIDetailCell> columnDataList : columnDataLists) {
                rowData.add(columnDataList.get(i));
            }
            rowDataLists.add(rowData);
        }
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

    @Override
    public boolean hasNext() {
        return dataIterator.hasNext();
    }

    @Override
    public List<BIDetailCell> next() {
        return dataIterator.next();
    }
}
