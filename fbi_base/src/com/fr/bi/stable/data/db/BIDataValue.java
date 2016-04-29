package com.fr.bi.stable.data.db;

/**
 * 表示数据库中的某个数据
 * Created by GUY on 2015/3/10.
 */

public class BIDataValue {

    private long row;
    private int col;
    private Object value;

    public BIDataValue(long row, int col, Object value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public long getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Object getValue() {
        return value;
    }
}