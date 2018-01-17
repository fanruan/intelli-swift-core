package com.fr.swift.adaptor.struct;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;

import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2018-1-16 17:02:20
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftRealDetailResult implements BIDetailTableResult {

    private Iterator<List<BIDetailCell>> dataIterator;
    private int rowCount;
    private int columnSize;

    public SwiftRealDetailResult(Iterator<List<BIDetailCell>> dataIterator, int rowCount, int columnSize) {
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
