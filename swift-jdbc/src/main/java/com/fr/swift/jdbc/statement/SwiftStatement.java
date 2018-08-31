package com.fr.swift.jdbc.statement;

import com.fr.general.jsqlparser.JSQLParserException;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.invoke.SqlInvoker;
import com.fr.swift.jdbc.parser.SqlParserFactory;
import com.fr.swift.jdbc.result.EmptyResultSet;
import com.fr.swift.jdbc.result.ResultSetWrapper;
import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.source.SwiftResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author pony
 * @date 2018/8/17
 */
public class SwiftStatement extends BaseSwiftStatement {
    private RpcCaller.SelectRpcCaller caller;
    private RpcCaller.MaintenanceRpcCaller maintenanceRpcCaller;
    private SwiftDatabase database;

    public SwiftStatement(SwiftDatabase database, RpcCaller.SelectRpcCaller caller, RpcCaller.MaintenanceRpcCaller maintenanceRpcCaller) {
        this.database = database;
        this.caller = caller;
        this.maintenanceRpcCaller = maintenanceRpcCaller;
    }

    public SwiftStatement(SwiftDatabase database) {
        this.database = database;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        SwiftResultSet resultSet = null;
        try {
            SqlInvoker invoke = SqlParserFactory.parseSql(sql, database, caller);
            if (null != invoke) {
                switch (invoke.getType()) {
                    case QUERY:
                        resultSet = (SwiftResultSet) invoke.invoke();
                        break;
                    default:
                        return new ResultSetWrapper(EmptyResultSet.INSTANCE);
                }
            } else {
                throw new SQLException(new SwiftJDBCNotSupportedException(sql));
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return new ResultSetWrapper(resultSet);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        try {
            SqlInvoker invoke = SqlParserFactory.parseSql(sql, database, maintenanceRpcCaller);
            if (null != invoke) {
                switch (invoke.getType()) {
                    case INSERT:
                    case CREATE_TABLE:
                    case DELETE:
                    case UPDATE:
                        return (Integer) invoke.invoke();
                    default:
                        return -1;
                }
            }
        } catch (JSQLParserException e) {
            throw new SQLException(e);
        }
        throw new SQLException(new SwiftJDBCNotSupportedException(sql));
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return executeUpdate(sql) >= 0;
    }
}
