package com.fr.bi.cal.report.io.iterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 2016/7/12.
 */
public class TableCellIterator {

    private volatile boolean isEnd = false;
    private int column;
    private int row;
    private List<StreamPagedIterator> iters;

    public TableCellIterator(int column, int row) {
        this.column = column;
        this.row = row;
        this.iters = new ArrayList<StreamPagedIterator>();
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public void setColumn(int currentColumn) {
        column = currentColumn;
    }

    public void setRow(int currentRow) {
        row = currentRow;
    }

    public StreamPagedIterator getIteratorByPage(int page) {
        if (iters.size() > page) {
            return iters.get(page);
        }

        StreamPagedIterator pagedIterator = new StreamPagedIterator();
        iters.add(pagedIterator);

        return pagedIterator;
    }

    public void finish() {
        for (StreamPagedIterator iter : iters) {
            iter.finish();
        }
        isEnd = true;
    }

}
