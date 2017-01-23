package com.fr.bi.etl.analysis.monitor;

/**
 * Created by daniel on 2017/1/22.
 */
public class BITablePosition {
    private int column = 0;
    private int row = 0;
    private final SimpleTable table;

    public BITablePosition(SimpleTable table) {
        this.table = table;
    }

    public SimpleTable getTable(){
        return  table;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
