package com.fr.swift.query.query;

import com.fr.swift.result.qrs.QueryResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/11/29.
 * 查询接口
 */
public interface Query<T extends QueryResultSet> {
    T getQueryResult() throws SQLException;
}
