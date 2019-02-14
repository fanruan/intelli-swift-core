package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import junit.framework.TestCase;

import java.util.HashSet;

/**
 * Created by lyon on 2018/12/11.
 */
public class WhereASTVisitorAdapterTest extends TestCase {

    public void testBetween() {
        String sql = "select * from cube.table_name where a between 0 and 233";
        QueryASTVisitorAdapter visitor = new QueryASTVisitorAdapter();
        SQLStatement stmt = SwiftSQLUtils.parseStatement(sql);
        stmt.accept(visitor);
        DetailQueryInfoBean bean = (DetailQueryInfoBean) visitor.getSelectionBean().getQueryInfoBean();
        FilterInfoBean filterInfoBean = bean.getFilter();
        assertEquals(SwiftDetailFilterType.NUMBER_IN_RANGE, filterInfoBean.getType());
        NumberInRangeFilterBean filter = (NumberInRangeFilterBean) filterInfoBean;
        assertEquals("a", filter.getColumn());
        assertFalse(filter.getFilterValue().isStartIncluded());
        assertFalse(filter.getFilterValue().isEndIncluded());
        assertEquals("0", filter.getFilterValue().getStart());
        assertEquals("233", filter.getFilterValue().getEnd());
    }

    public void testIn() {
        String sql = "select * from cube.table_name where a in ('a1', 'a2')";
        QueryASTVisitorAdapter visitor = new QueryASTVisitorAdapter();
        SQLStatement stmt = SwiftSQLUtils.parseStatement(sql);
        stmt.accept(visitor);
        DetailQueryInfoBean bean = (DetailQueryInfoBean) visitor.getSelectionBean().getQueryInfoBean();
        FilterInfoBean filterInfoBean = bean.getFilter();
        assertEquals(SwiftDetailFilterType.IN, filterInfoBean.getType());
        InFilterBean filter = (InFilterBean) filterInfoBean;
        assertEquals("a", filter.getColumn());
        assertEquals(new HashSet() {{
            add("a1");
            add("a2");
        }}, filter.getFilterValue());
    }

}