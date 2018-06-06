package com.fr.swift.result;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;

/**
 * @author pony
 * @date 2017/12/6
 */
public abstract class DetailResultSet implements SwiftResultSet {
    public static final DetailResultSet EMPTY = new DetailResultSet() {
        @Override
        public boolean next() {
            return false;
        }

        @Override
        public Row getRowData() {
            return new ListBasedRow(new ArrayList<Object>());
        }
    };
    /**
     * 已拿到的行数
     */
    protected int rowCount = -1;

    /**
     * 结果集的行数
     */
    protected int maxRow = 0;

    @Override
    public boolean next() {
        // TODO: 2018/6/5 这个地方改变rowCount感觉不太好，和标准迭代器的hashNext统一一下比较好
        return ++rowCount < maxRow;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return null;
    }

    @Override
    public void close() {

    }

    public int getRowSize() {
        return maxRow;
    }
}