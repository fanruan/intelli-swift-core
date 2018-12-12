package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.adaptor.bean.CreationBean;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import org.junit.Test;

import java.sql.Types;

import static org.junit.Assert.assertEquals;

/**
 * Created by lyon on 2018/12/11.
 */
public class CreationASTVisitorAdapterTest {

    @Test
    public void test() {
        String sql = "create table cube.tbl_name (a int, b bigint, c double, d timestamp, e date, f varchar)";
        CreationASTVisitorAdapter visitor = new CreationASTVisitorAdapter();
        SQLStatement stmt = SwiftSQLUtils.parseStatement(sql);
        stmt.accept(visitor);
        CreationBean creationBean = visitor.getCreationBean();
        assertEquals("tbl_name", creationBean.getTableName());
        assertEquals("cube", creationBean.getSchema());
        assertEquals(6, creationBean.getFields().size());
        assertEquals(Types.INTEGER, creationBean.getFields().get(0).getColumnType());
        assertEquals(Types.BIGINT, creationBean.getFields().get(1).getColumnType());
        assertEquals(Types.DOUBLE, creationBean.getFields().get(2).getColumnType());
        assertEquals(Types.TIMESTAMP, creationBean.getFields().get(3).getColumnType());
        assertEquals(Types.DATE, creationBean.getFields().get(4).getColumnType());
        assertEquals(Types.VARCHAR, creationBean.getFields().get(5).getColumnType());
        assertEquals("a", creationBean.getFields().get(0).getColumnName());
        assertEquals("b", creationBean.getFields().get(1).getColumnName());
        assertEquals("c", creationBean.getFields().get(2).getColumnName());
        assertEquals("d", creationBean.getFields().get(3).getColumnName());
        assertEquals("e", creationBean.getFields().get(4).getColumnName());
        assertEquals("f", creationBean.getFields().get(5).getColumnName());
    }
}