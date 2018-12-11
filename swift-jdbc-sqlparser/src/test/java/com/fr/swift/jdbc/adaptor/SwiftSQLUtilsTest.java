package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.druid.sql.SQLUtils;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExprTableSource;
import com.fr.swift.jdbc.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by lyon on 2018/12/11.
 */
public class SwiftSQLUtilsTest extends TestCase {

    public void testGetTableName() {
        String sql = "drop table cube.tbl_name";
        SQLExprTableSource tableSource = ((SQLDropTableStatement) SQLUtils.parseStatements(sql, JdbcConstants.SWIFT).get(0)).getTableSources().get(0);
        String[] table = SwiftSQLUtils.getTableName(tableSource);
        assertEquals("tbl_name", table[0]);
        assertEquals("cube", table[1]);
    }

    public void testGetSqlType() {
        String sql = "select * from tbl_name";
        assertEquals(SwiftSQLType.SELECT, SwiftSQLUtils.getSqlType(sql));
        sql = "insert into tbl_name (a, b) values ('a1', 2)";
        assertEquals(SwiftSQLType.INSERT, SwiftSQLUtils.getSqlType(sql));
        sql = "create table cube.tbl_name (a int, b varchar)";
        assertEquals(SwiftSQLType.CREATE, SwiftSQLUtils.getSqlType(sql));
        sql = "delete from tbl_name where a = 233";
        assertEquals(SwiftSQLType.DELETE, SwiftSQLUtils.getSqlType(sql));
        sql = "drop table tbl_name";
        assertEquals(SwiftSQLType.DROP, SwiftSQLUtils.getSqlType(sql));
    }
}