package com.fr.bi.cal.report.io.iterator;

/**
 * Created by daniel on 2016/7/12.
 */
public class TableCellIterator {

    private volatile boolean isEnd = false;
    private int column;
    private int row;
    private StreamPagedIterator iter;

    public TableCellIterator(int column, int row) {
        this.column = column;
        this.row = row;
        this.iter = new StreamPagedIterator();
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

    public StreamPagedIterator getPageIterator() {
        return iter;
    }

    public void finish() {
        iter.finish();
        isEnd = true;
    }

}
