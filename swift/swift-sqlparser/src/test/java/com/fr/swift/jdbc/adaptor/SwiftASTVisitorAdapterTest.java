package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.druid.sql.SQLUtils;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.jdbc.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by lyon on 2018/12/12.
 */
public class SwiftASTVisitorAdapterTest extends TestCase {

    public void testGetSqlType() {
        String sql = "select * from tbl_name";

        assertEquals(SwiftSQLType.SELECT, getType(sql));
        sql = "insert into tbl_name (a, b) values ('a1', 2)";
        assertEquals(SwiftSQLType.INSERT, getType(sql));
        sql = "create table cube.tbl_name (a int, b varchar)";
        assertEquals(SwiftSQLType.CREATE, getType(sql));
        sql = "delete from tbl_name where a = 233";
        assertEquals(SwiftSQLType.DELETE, getType(sql));
        sql = "drop table tbl_name";
        assertEquals(SwiftSQLType.DROP, getType(sql));
    }

    private static SwiftSQLType getType(String sql) {
        SwiftASTVisitorAdapter visitor = new SwiftASTVisitorAdapter();
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SWIFT);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        return visitor.getSqlType();
    }
}