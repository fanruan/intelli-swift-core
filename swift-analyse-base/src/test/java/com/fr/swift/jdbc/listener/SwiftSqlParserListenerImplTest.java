package com.fr.swift.jdbc.listener;

import com.fr.swift.jdbc.adaptor.bean.ColumnBean;
import com.fr.swift.jdbc.adaptor.bean.CreationBean;
import com.fr.swift.jdbc.adaptor.bean.DeletionBean;
import com.fr.swift.jdbc.adaptor.bean.DropBean;
import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.adaptor.bean.SelectionBean;
import com.fr.swift.jdbc.adaptor.bean.TruncateBean;
import com.fr.swift.jdbc.antlr4.SwiftSqlParseUtil;
import com.fr.swift.jdbc.listener.handler.SwiftSqlBeanHandler;
import com.fr.swift.query.info.bean.element.AggregationBean;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.source.Row;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2019-07-22
 */
public class SwiftSqlParserListenerImplTest {
    @Test
    public void select() {
        String selectAllColumn = "select * from tbl_name";
        String select = "select id, colName from tbl_name";
        assertTrue(SwiftSqlParseUtil.isSelect(selectAllColumn));
        assertTrue(SwiftSqlParseUtil.isSelect(select));
        SwiftSqlBeanHandler mock = Mockito.mock(SwiftSqlBeanHandler.class);
        Mockito.doAnswer(new Answer() {
            int time = 0;

            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                SelectionBean selectionBean = invocationOnMock.getArgument(0);
                assertEquals("tbl_name", selectionBean.getTableName());
                QueryInfoBean queryInfoBean = selectionBean.getQueryInfoBean();
                assertNotNull(queryInfoBean);
                assertTrue(queryInfoBean instanceof DetailQueryInfoBean);
                assertNull(((DetailQueryInfoBean) queryInfoBean).getFilter());
                List<DimensionBean> dimensions = ((DetailQueryInfoBean) queryInfoBean).getDimensions();
                if (++time < 2) {
                    assertEquals(1, dimensions.size());
                    assertEquals(DimensionType.DETAIL_ALL_COLUMN, dimensions.get(0).getType());
                } else {
                    assertEquals(2, dimensions.size());
                    assertEquals("id", dimensions.get(0).getColumn());
                    assertEquals("colName", dimensions.get(1).getColumn());
                }
                return null;
            }
        }).when(mock).handle(Mockito.any(SelectionBean.class));
        SwiftSqlParseUtil.parse(selectAllColumn, new SwiftSqlParserListenerImpl(mock));
        SwiftSqlParseUtil.parse(select, new SwiftSqlParserListenerImpl(mock));
        Mockito.verify(mock, Mockito.times(2)).handle(Mockito.any(SelectionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DropBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(CreationBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(InsertionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(TruncateBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DeletionBean.class));
    }

    @Test
    public void selectAs() {
        String select = "select city, sum(price) as sumPrice from tbl_name";
        assertTrue(SwiftSqlParseUtil.isSelect(select));
        SwiftSqlBeanHandler mock = Mockito.mock(SwiftSqlBeanHandler.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                SelectionBean selectionBean = invocationOnMock.getArgument(0);
                assertEquals("tbl_name", selectionBean.getTableName());
                QueryInfoBean queryInfoBean = selectionBean.getQueryInfoBean();
                assertNotNull(queryInfoBean);
                assertTrue(queryInfoBean instanceof GroupQueryInfoBean);
                assertNull(((GroupQueryInfoBean) queryInfoBean).getFilter());
                List<DimensionBean> dimensions = ((GroupQueryInfoBean) queryInfoBean).getDimensions();
                List<AggregationBean> aggregations = ((GroupQueryInfoBean) queryInfoBean).getAggregations();
                assertEquals(1, dimensions.size());
                assertEquals(1, aggregations.size());
                assertEquals("city", dimensions.get(0).getColumn());
                assertEquals("price", aggregations.get(0).getColumn());
                assertEquals("sumPrice", aggregations.get(0).getAlias());
                return null;
            }
        }).when(mock).handle(Mockito.any(SelectionBean.class));
        SwiftSqlParseUtil.parse(select, new SwiftSqlParserListenerImpl(mock));
        Mockito.verify(mock).handle(Mockito.any(SelectionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DropBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(CreationBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(InsertionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(TruncateBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DeletionBean.class));
    }

    @Test
    public void selectWhere() {
        String select = "select city, sum(price) as sumPrice from tbl_name where city in ('南京', '无锡')";
        assertTrue(SwiftSqlParseUtil.isSelect(select));
        SwiftSqlBeanHandler mock = Mockito.mock(SwiftSqlBeanHandler.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                SelectionBean selectionBean = invocationOnMock.getArgument(0);
                assertEquals("tbl_name", selectionBean.getTableName());
                QueryInfoBean queryInfoBean = selectionBean.getQueryInfoBean();
                assertNotNull(queryInfoBean);
                assertTrue(queryInfoBean instanceof GroupQueryInfoBean);
                FilterInfoBean filter = ((GroupQueryInfoBean) queryInfoBean).getFilter();
                assertNotNull(filter);
                assertTrue(filter instanceof InFilterBean);
                assertEquals("city", ((InFilterBean) filter).getColumn());
                Set<String> filterValue = ((InFilterBean) filter).getFilterValue();
                assertEquals(2, filterValue.size());
                assertTrue(filterValue.containsAll(Arrays.asList("南京", "无锡")));
                List<DimensionBean> dimensions = ((GroupQueryInfoBean) queryInfoBean).getDimensions();
                List<AggregationBean> aggregations = ((GroupQueryInfoBean) queryInfoBean).getAggregations();
                assertEquals(1, dimensions.size());
                assertEquals(1, aggregations.size());
                assertEquals("city", dimensions.get(0).getColumn());
                assertEquals("price", aggregations.get(0).getColumn());
                assertEquals("sumPrice", aggregations.get(0).getAlias());
                return null;
            }
        }).when(mock).handle(Mockito.any(SelectionBean.class));
        SwiftSqlParseUtil.parse(select, new SwiftSqlParserListenerImpl(mock));
        Mockito.verify(mock).handle(Mockito.any(SelectionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DropBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(CreationBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(InsertionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(TruncateBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DeletionBean.class));
    }


    @Test
    public void insert() {
        String insert = "insert into tbl_name (id, name) values (1, 'anchore'), (2, 'lucifer')";
        assertFalse(SwiftSqlParseUtil.isSelect(insert));
        SwiftSqlBeanHandler mock = Mockito.mock(SwiftSqlBeanHandler.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                InsertionBean insertionBean = invocationOnMock.getArgument(0);
                assertEquals("tbl_name", insertionBean.getTableName());
                List<String> fields = insertionBean.getFields();
                assertEquals(2, fields.size());
                assertEquals("id", fields.get(0));
                assertEquals("name", fields.get(1));
                List<Row> rows = insertionBean.getRows();
                assertEquals(2, rows.size());
                assertEquals(1.0, rows.get(0).getValue(0));
                assertEquals("anchore", rows.get(0).getValue(1));
                assertEquals(2.0, rows.get(1).getValue(0));
                assertEquals("lucifer", rows.get(1).getValue(1));
                return null;
            }
        }).when(mock).handle(Mockito.any(InsertionBean.class));

        SwiftSqlParseUtil.parse(insert, new SwiftSqlParserListenerImpl(mock));
        Mockito.verify(mock).handle(Mockito.any(InsertionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DropBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(CreationBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(SelectionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(TruncateBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DeletionBean.class));
    }

    @Test
    public void delete() {
        String insert = "delete from tbl_name where price between 100 and 200";
        assertFalse(SwiftSqlParseUtil.isSelect(insert));
        SwiftSqlBeanHandler mock = Mockito.mock(SwiftSqlBeanHandler.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                DeletionBean delete = invocationOnMock.getArgument(0);
                assertEquals("tbl_name", delete.getTableName());
                FilterInfoBean filter = delete.getFilter();
                assertNotNull(filter);
                assertTrue(filter instanceof NumberInRangeFilterBean);
                assertEquals("price", ((NumberInRangeFilterBean) filter).getColumn());
                assertTrue(((NumberInRangeFilterBean) filter).getFilterValue().isStartIncluded());
                assertTrue(((NumberInRangeFilterBean) filter).getFilterValue().isEndIncluded());
                assertEquals("100", ((NumberInRangeFilterBean) filter).getFilterValue().getStart());
                assertEquals("200", ((NumberInRangeFilterBean) filter).getFilterValue().getEnd());
                return null;
            }
        }).when(mock).handle(Mockito.any(DeletionBean.class));

        SwiftSqlParseUtil.parse(insert, new SwiftSqlParserListenerImpl(mock));
        Mockito.verify(mock).handle(Mockito.any(DeletionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DropBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(CreationBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(SelectionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(TruncateBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(InsertionBean.class));
    }

    @Test
    public void drop() {
        String insert = "drop table tbl_name";
        assertFalse(SwiftSqlParseUtil.isSelect(insert));
        SwiftSqlBeanHandler mock = Mockito.mock(SwiftSqlBeanHandler.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                DropBean delete = invocationOnMock.getArgument(0);
                assertEquals("tbl_name", delete.getTableName());
                return null;
            }
        }).when(mock).handle(Mockito.any(DropBean.class));

        SwiftSqlParseUtil.parse(insert, new SwiftSqlParserListenerImpl(mock));
        Mockito.verify(mock).handle(Mockito.any(DropBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DeletionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(CreationBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(SelectionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(TruncateBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(InsertionBean.class));
    }

    @Test
    public void truncate() {
        String insert = "truncate tbl_name";
        assertFalse(SwiftSqlParseUtil.isSelect(insert));
        SwiftSqlBeanHandler mock = Mockito.mock(SwiftSqlBeanHandler.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                TruncateBean delete = invocationOnMock.getArgument(0);
                assertEquals("tbl_name", delete.getTableName());
                return null;
            }
        }).when(mock).handle(Mockito.any(TruncateBean.class));

        SwiftSqlParseUtil.parse(insert, new SwiftSqlParserListenerImpl(mock));
        Mockito.verify(mock).handle(Mockito.any(TruncateBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DeletionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(CreationBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(SelectionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DropBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(InsertionBean.class));
    }

    @Test
    public void create() {
        String create = "create table tbl_name (id integer, age integer, name varchar)";
        assertFalse(SwiftSqlParseUtil.isSelect(create));
        SwiftSqlBeanHandler mock = Mockito.mock(SwiftSqlBeanHandler.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                CreationBean delete = invocationOnMock.getArgument(0);
                assertEquals("tbl_name", delete.getTableName());
                List<ColumnBean> fields = delete.getFields();
                assertEquals(3, fields.size());
                assertEquals("id", fields.get(0).getColumnName());
                assertEquals(Types.INTEGER, fields.get(0).getColumnType());
                assertEquals("age", fields.get(1).getColumnName());
                assertEquals(Types.INTEGER, fields.get(1).getColumnType());
                assertEquals("name", fields.get(2).getColumnName());
                assertEquals(Types.VARCHAR, fields.get(2).getColumnType());
                return null;
            }
        }).when(mock).handle(Mockito.any(CreationBean.class));

        SwiftSqlParseUtil.parse(create, new SwiftSqlParserListenerImpl(mock));
        Mockito.verify(mock).handle(Mockito.any(CreationBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DeletionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(TruncateBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(SelectionBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(DropBean.class));
        Mockito.verify(mock, Mockito.never()).handle(Mockito.any(InsertionBean.class));
    }
}