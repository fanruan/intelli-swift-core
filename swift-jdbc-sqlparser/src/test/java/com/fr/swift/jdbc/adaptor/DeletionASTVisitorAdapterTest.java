package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.adaptor.bean.DeletionBean;
import com.fr.swift.jdbc.adaptor.bean.DropBean;
import com.fr.swift.jdbc.druid.sql.SQLUtils;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.jdbc.druid.util.JdbcConstants;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

/**
 * Created by lyon on 2018/12/11.
 */
public class DeletionASTVisitorAdapterTest extends TestCase {

    public void testDelete() {
        String sql = "delete from cube.tbl_name where a = 233";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SWIFT);
        DeletionASTVisitorAdapter visitor = new DeletionASTVisitorAdapter();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        DeletionBean deletionBean = visitor.getDeletionBean();
        assertEquals("tbl_name", deletionBean.getTableName());
        assertEquals("cube", deletionBean.getSchema());
        FilterInfoBean filter = (FilterInfoBean) deletionBean.getFilter();
        assertEquals(SwiftDetailFilterType.IN, filter.getType());
        assertEquals(Collections.singleton("233"), filter.getFilterValue());
    }

    public void testDrop() {
        String sql = "drop table cube.tbl_name";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SWIFT);
        DeletionASTVisitorAdapter visitor = new DeletionASTVisitorAdapter();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        DropBean dropBean = visitor.getDropBean();
        assertEquals("tbl_name", dropBean.getTableName());
        assertEquals("cube", dropBean.getSchema());
    }
}