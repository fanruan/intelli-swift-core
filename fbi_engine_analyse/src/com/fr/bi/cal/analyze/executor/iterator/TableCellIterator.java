package com.fr.bi.cal.analyze.executor.iterator;

import com.fr.bi.cal.report.io.BIExportUtils;

/**
 * Created by daniel on 2016/7/12.
 */
public class TableCellIterator {

    private volatile boolean isEnd = false;
    private int column;
    private int row;
    private StreamPagedIterator[] iters = null;

    public TableCellIterator(int column, int row) {
        this.column = column;
        this.row = row;
        this.iters = new StreamPagedIterator[BIExportUtils.createExcel2007Page(row)];
        for (int i = 0; i < iters.length; i++) {
            this.iters[i] = new StreamPagedIterator();
        }
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public StreamPagedIterator getIteratorByPage(int page) {
        if (page < 0 || page >= iters.length) {
            return null;
        }
        return iters[page];
    }

    public void finish() {
        for (int i = 0; i < iters.length; i++) {
            this.iters[i].finish();
        }
        isEnd = true;
    }

}
