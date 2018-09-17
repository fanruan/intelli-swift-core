package com.fr.swift.query.query;

import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author pony
 * @date 2017/12/20
 */
public interface QueryRunner {

    /**
     * 本地查询
     *
     * @param info
     * @return
     * @throws SQLException
     */
    SwiftResultSet getQueryResult(QueryBean info) throws Exception;

}
