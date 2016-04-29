package com.finebi.cube;

/**
 * 行号数据的分装。
 * 数据库或者类似表结构中，的行号。
 * This class created on 2016/3/5.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRowNumber {
    private long rowNumberValue;

    public BICubeRowNumber(long rowNumberValue) {
        this.rowNumberValue = rowNumberValue;
    }

    public long getRowNumberValue() {
        return rowNumberValue;
    }
}
