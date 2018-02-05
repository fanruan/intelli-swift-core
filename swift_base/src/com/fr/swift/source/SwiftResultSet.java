package com.fr.swift.source;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/5.
 * 为以后适配ResultSet做准备
 */
public interface SwiftResultSet{
    void close() throws SQLException;

    boolean next() throws SQLException;

    SwiftMetaData getMetaData() throws SQLException;

    Row getRowData() throws SQLException;
}
