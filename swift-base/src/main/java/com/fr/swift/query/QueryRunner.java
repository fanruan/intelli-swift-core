package com.fr.swift.query;

import com.fr.swift.source.SwiftResultSet;

import java.net.URI;
import java.sql.SQLException;

/**
 *
 * @author pony
 * @date 2017/12/20
 */
public interface QueryRunner {

    /**
     * 本地查询
     *
     * @param info
     * @param <T>
     * @return
     * @throws SQLException
     */
    <T extends SwiftResultSet> T getQueryResult(QueryInfo<T> info) throws SQLException;

    /**
     * 远程查询
     *
     * @param info
     * @param remoteURI
     * @param <T>
     * @return
     * @throws SQLException
     */
    <T extends SwiftResultSet> T getRemoteQueryResult(QueryInfo<T> info, URI remoteURI) throws SQLException;
}
