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
}