package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.post.RowSortQueryInfoBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.sort.SortType;
import junit.framework.TestCase;

/**
 * Created by lyon on 2018/12/7.
 */
public class QueryASTVisitorAdapterTest extends TestCase {

    public void testSelectStar() {
        String sql = "select * from cube.table_name";
        QueryASTVisitorAdapter visitor = new QueryASTVisitorAdapter();
        SQLStatement stmt = SwiftSQLUtils.parseStatement(sql);
        stmt.accept(visitor);
        DetailQueryInfoBean bean = (DetailQueryInfoBean) visitor.getSelectionBean().getQueryInfoBean();
        assertEquals(QueryType.DETAIL, bean.getQueryType());
        assertEquals("table_name", bean.getTableName());
        assertEquals(1, bean.getDimensions().size());
        assertEquals(DimensionType.DETAIL_ALL_COLUMN, bean.getDimensions().get(0).getType());
    }

    public void testSelectGroup() {
        String sql = "select a, aa, HLL_DISTINCT(b) as bb " +
                "from table_a " +
                "where a > 233 and aa <= 999 " +
                "group by a asc, aa desc " +
                "order by bb asc";
        QueryASTVisitorAdapter visitor = new QueryASTVisitorAdapter();
        SQLStatement stmt = SwiftSQLUtils.parseStatement(sql);
        stmt.accept(visitor);
        GroupQueryInfoBean bean = (GroupQueryInfoBean) visitor.getSelectionBean().getQueryInfoBean();
        assertEquals(QueryType.GROUP, bean.getQueryType());
        assertEquals("table_a", bean.getTableName());
        // dimensions
        assertEquals(2, bean.getDimensions().size());
        assertEquals("a", bean.getDimensions().get(0).getColumn());
        assertEquals(SortType.ASC, bean.getDimensions().get(0).getSortBean().getType());
        assertEquals("aa", bean.getDimensions().get(1).getColumn());
        assertEquals(SortType.DESC, bean.getDimensions().get(1).getSortBean().getType());
        // metrics
        assertEquals(1, bean.getAggregations().size());
        assertEquals("b", bean.getAggregations().get(0).getColumn());
        assertEquals("bb", bean.getAggregations().get(0).getAlias());
        assertEquals(AggregatorType.HLL_DISTINCT, bean.getAggregations().get(0).getType());
        // filter
        FilterInfoBean filter = bean.getFilter();
        assertEquals(SwiftDetailFilterType.AND, filter.getType());
        assertEquals(2, ((AndFilterBean) filter).getFilterValue().size());
        assertEquals(SwiftDetailFilterType.NUMBER_IN_RANGE, ((AndFilterBean) filter).getFilterValue().get(0).getType());
        assertEquals(SwiftDetailFilterType.NUMBER_IN_RANGE, ((AndFilterBean) filter).getFilterValue().get(1).getType());
        NumberInRangeFilterBean filter1 = (NumberInRangeFilterBean) ((AndFilterBean) filter).getFilterValue().get(0);
        NumberInRangeFilterBean filter2 = (NumberInRangeFilterBean) ((AndFilterBean) filter).getFilterValue().get(1);
        assertEquals("a", filter1.getColumn());
        assertEquals("aa", filter2.getColumn());
        assertFalse(filter1.getFilterValue().isStartIncluded());
        assertFalse(filter1.getFilterValue().isEndIncluded());
        assertEquals("233", filter1.getFilterValue().getStart());
        assertFalse(filter2.getFilterValue().isStartIncluded());
        assertTrue(filter2.getFilterValue().isEndIncluded());
        assertEquals("999", filter2.getFilterValue().getEnd());
        // sort
        assertEquals(1, bean.getPostAggregations().size());
        assertEquals(PostQueryType.ROW_SORT, bean.getPostAggregations().get(0).getType());
        RowSortQueryInfoBean sort = (RowSortQueryInfoBean) bean.getPostAggregations().get(0);
        assertEquals(1, sort.getSortBeans().size());
        assertEquals(SortType.ASC, sort.getSortBeans().get(0).getType());
        assertEquals("bb", sort.getSortBeans().get(0).getName());
    }
}
