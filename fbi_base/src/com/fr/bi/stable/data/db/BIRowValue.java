package com.fr.bi.stable.data.db;

/**
 * 表示一行的数据
 * Created by GUY on 2015/4/21.
 */
public class BIRowValue {
    private long row;

    private Object[] values;

    public BIRowValue(Object[] values) {
        this.values = values;
    }

    public BIRowValue(long row, Object[] values) {
        this.row = row;
        this.values = values;
    }

    public long getRow() {
        return row;
    }

    public void setRow(long row) {
        this.row = row;
    }

    public Object[] getValues() {
        return values;
    }
}