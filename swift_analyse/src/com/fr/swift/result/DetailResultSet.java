package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;


/**
 * Created by pony on 2017/12/6.
 */
public abstract class DetailResultSet implements SwiftResultSet {

    /**
     * 已拿到的行数
     */
    protected int rowCount = -1;

    /**
     * 结果集的行数
     */
    protected int maxRow = 0;

    public DetailResultSet() {

    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public boolean next() throws SQLException {

        return ++rowCount < maxRow;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Row getRowData() throws SQLException {
        return null;
    }


}
