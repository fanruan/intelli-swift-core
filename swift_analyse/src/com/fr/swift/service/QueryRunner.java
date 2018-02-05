package com.fr.swift.service;

import com.fr.swift.cal.QueryInfo;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/20.
 */
public interface QueryRunner {
    <T extends SwiftResultSet> T getQueryResult(QueryInfo<T> info) throws SQLException;
}
