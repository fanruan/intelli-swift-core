package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.source.ListBasedRow;
import junit.framework.TestCase;

import java.util.Arrays;


/**
 * Created by lyon on 2018/12/11.
 */
public class InsertionASTVisitorAdapterTest extends TestCase {

    public void test() {
        String sql = "insert into cube.tbl_name (a, b, c) values ('a', 'b', 233), ('a1', 'b1', 234)";
        InsertionASTVisitorAdapter visitor = new InsertionASTVisitorAdapter("");
        SQLStatement stmt = SwiftSQLUtils.parseStatement(sql);
        stmt.accept(visitor);
        InsertionBean bean = visitor.getInsertionBean();
        assertEquals("tbl_name", bean.getTableName());
        assertEquals("cube", bean.getSchema());
        assertEquals(Arrays.asList("a", "b", "c"), bean.getFields());
        assertEquals(Arrays.asList(new ListBasedRow(Arrays.asList("a", "b", 233)), new ListBasedRow(Arrays.asList("a1", "b1", 234))), bean.getRows());
    }
}