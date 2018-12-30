package com.fr.swift.jdbc.sql;

import com.fr.swift.jdbc.checker.GrammarChecker;
import com.fr.swift.jdbc.info.SqlRequestInfo;
import com.fr.swift.jdbc.rpc.JdbcExecutor;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018-12-03
 */
public abstract class BaseSwiftStatement implements SwiftStatement {
    protected BaseSwiftConnection connection;
    protected GrammarChecker grammarChecker;

    BaseSwiftStatement(BaseSwiftConnection connection) {
        this.connection = connection;
        this.grammarChecker = connection.getConfig().grammarChecker();
    }

    <T> T execute(SqlRequestInfo info, JdbcExecutor executor) throws SQLException {
        info.setDatabase(connection.getConfig().swiftDatabase());
        info.setAuthCode(connection.driver.holder.getAuthCode());
        return (T) connection.executeQueryInternal(info, executor);
    }
}
