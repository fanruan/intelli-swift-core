package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.JSQLParserException;
import com.fr.general.jsqlparser.parser.CCJSqlParserUtil;
import com.fr.general.jsqlparser.statement.Statement;
import com.fr.general.jsqlparser.statement.select.Select;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.invoke.SqlInvoker;
import com.fr.swift.jdbc.proxy.invoke.JdbcCaller;

import java.io.StringReader;

/**
 * Created by pony on 2018/8/17.
 */
public class SqlParserFactory {
    public static SqlInvoker parseSql(String sql, SwiftDatabase schema, JdbcCaller.SelectJdbcCaller caller, JdbcCaller.MaintenanceJdbcCaller maintain) throws JSQLParserException {
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
}
