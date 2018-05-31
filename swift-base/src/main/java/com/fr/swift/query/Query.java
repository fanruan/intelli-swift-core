package com.fr.swift.query;

import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/11/29.
 * 查询接口
 */
public interface Query<T extends SwiftResultSet> {
    T getQueryResult() throws SQLException;
}
