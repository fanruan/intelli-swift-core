package com.fr.swift.query.session;

import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.source.SwiftResultSet;

import java.io.Closeable;
import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/6/19
 */
public interface Session extends Closeable {
    String getSessionId();

    <T extends SwiftResultSet> T executeQuery(QueryInfo<T> queryInfo, int segmentOrder) throws SQLException;

    <T extends SwiftResultSet> T executeQuery(QueryInfo<T> queryInfo) throws SQLException;

    @Override
    void close();

    boolean isClose();

    void cleanCache(boolean force);
}
