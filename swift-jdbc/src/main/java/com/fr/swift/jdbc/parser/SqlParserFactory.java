package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.JSQLParserException;
import com.fr.general.jsqlparser.parser.CCJSqlParserUtil;
import com.fr.general.jsqlparser.statement.Statement;
import com.fr.general.jsqlparser.statement.select.Select;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.SwiftJdbcConstants;
import com.fr.swift.jdbc.invoke.SqlInvoker;
import com.fr.swift.jdbc.invoke.impl.SelectInvokerImpl;
import com.fr.swift.jdbc.proxy.invoke.JdbcCaller;

import java.io.StringReader;

/**
 *
 * @author pony
 * @date 2018/8/17
 */
public class SqlParserFactory {
    public static SqlInvoker parseSql(String sql, SwiftDatabase schema, JdbcCaller.SelectJdbcCaller caller, JdbcCaller.MaintenanceJdbcCaller maintain) throws JSQLParserException {
        if (isJsonQuery(sql)) {
            return new SelectInvokerImpl(sql.substring(SwiftJdbcConstants.JSON_QUERY_HEAD_LENGTH), schema, caller);
        }
        Statement stmt = CCJSqlParserUtil.parse(new StringReader(sql));
        SwiftSqlVisitor visitor = null;
        if (stmt instanceof Select) {
            visitor = new SwiftSqlVisitor(schema, caller);
        } else {
            visitor = new SwiftSqlVisitor(schema, maintain);
        }
        stmt.accept(visitor);
        return visitor.get();
    }

    private static boolean isJsonQuery(String sql) {
        return sql.trim().toLowerCase().startsWith(SwiftJdbcConstants.KeyWords.JSON_QUERY);
    }
}
