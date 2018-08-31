package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.JSQLParserException;
import com.fr.general.jsqlparser.parser.CCJSqlParserUtil;
import com.fr.general.jsqlparser.statement.Statement;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.invoke.SqlInvoker;
import com.fr.swift.jdbc.rpc.RpcCaller;

import java.io.StringReader;

/**
 * Created by pony on 2018/8/17.
 */
public class SqlParserFactory {
    public static SqlInvoker parseSql(String sql, SwiftDatabase schema, RpcCaller caller) throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse(new StringReader(sql));
        SwiftSqlVisitor visitor = new SwiftSqlVisitor(schema, caller);
        stmt.accept(visitor);
        return visitor.get();
    }
}
