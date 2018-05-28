package com.fr.swift.service;

import com.fr.swift.cal.info.QueryInfo;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/20.
 */
@Deprecated
public class QueryRunnerProvider {
    private static QueryRunnerProvider ourInstance = new QueryRunnerProvider();

    public static QueryRunnerProvider getInstance() {
        return ourInstance;
    }

    private QueryRunner runner;

    private QueryRunnerProvider() {
    }

    protected void registerRunner(QueryRunner runner){
        this.runner = runner;
    }

    public <T extends SwiftResultSet> T executeQuery(QueryInfo<T> info) throws SQLException {
        return runner.executeQuery(info);
    }
}
