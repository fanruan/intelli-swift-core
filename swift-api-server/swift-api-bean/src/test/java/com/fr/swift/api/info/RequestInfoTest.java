package com.fr.swift.api.info;

import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AllShowFilterBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import org.junit.Test;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2019-01-03
 */
public class RequestInfoTest {

    @Test
    public void createTableRequestInfoTest() throws Exception {
        CreateTableRequestInfo info = new CreateTableRequestInfo();
        info.setTable("table");
        info.setColumns(Arrays.asList(new Column("id", Types.BIGINT), new Column("name", Types.VARCHAR)));
        info.setAuthCode("authCode");
        info.setDatabase(SwiftDatabase.CUBE);
        String json = JsonBuilder.writeJsonString(info);
        CreateTableRequestInfo newInfo = JsonBuilder.readValue(json, CreateTableRequestInfo.class);
        assertEquals(info.getTable(), newInfo.getTable());
        assertEquals(info.getDatabase(), newInfo.getDatabase());
        assertEquals(info.getAuthCode(), newInfo.getAuthCode());
        List<Column> expect = info.getColumns();
        List<Column> real = newInfo.getColumns();
        assertEquals(expect.size(), real.size());
        for (int i = 0; i < expect.size(); i++) {
            assertEquals(expect.get(i), real.get(i));
        }
    }

    @Test
    public void deleteRequestInfoTest() throws Exception {
        FilterBean filterBean = new AllShowFilterBean();
        DeleteRequestInfo info = new DeleteRequestInfo();
        info.setTable("table");
        info.setWhere(JsonBuilder.writeJsonString(filterBean));
        info.setAuthCode("authCode");
        info.setDatabase(SwiftDatabase.CUBE);
        String json = JsonBuilder.writeJsonString(info);
        DeleteRequestInfo newInfo = JsonBuilder.readValue(json, DeleteRequestInfo.class);
        assertEquals(info.getTable(), newInfo.getTable());
        assertEquals(info.getDatabase(), newInfo.getDatabase());
        assertEquals(info.getAuthCode(), newInfo.getAuthCode());
        String real = newInfo.getWhere();
        assertTrue(JsonBuilder.readValue(real, FilterInfoBean.class) instanceof AllShowFilterBean);
    }

    @Test
    public void insertRequestInfoTest() throws Exception {
        InsertRequestInfo info = new InsertRequestInfo();
        info.setTable("table");
        info.setSelectFields(Arrays.asList("id", "name"));
        info.setData(Arrays.<Row>asList(new ListBasedRow(1L, "anchor"), new ListBasedRow(2L, "mike")));
        info.setAuthCode("authCode");
        info.setDatabase(SwiftDatabase.CUBE);
        String json = JsonBuilder.writeJsonString(info);
        InsertRequestInfo newInfo = JsonBuilder.readValue(json, InsertRequestInfo.class);
        assertEquals(info.getTable(), newInfo.getTable());
        assertEquals(info.getDatabase(), newInfo.getDatabase());
        assertEquals(info.getAuthCode(), newInfo.getAuthCode());
        List<Row> expect = info.getData();
        List<Row> real = newInfo.getData();
        assertEquals(expect.size(), real.size());
        for (int i = 0; i < expect.size(); i++) {
            Row expectRow = expect.get(i);
            Row realRow = real.get(i);
            assertEquals(expectRow, realRow);
        }
        List<String> expectSelect = info.getSelectFields();
        List<String> realSelect = newInfo.getSelectFields();
        assertEquals(expectSelect.size(), realSelect.size());
        for (int i = 0; i < expectSelect.size(); i++) {
            assertEquals(expectSelect.get(i), realSelect.get(i));
        }
    }

    @Test
    public void queryRequestInfoTest() throws Exception {
        QueryRequestInfo info = new QueryRequestInfo();
        DetailQueryInfoBean queryBean = new DetailQueryInfoBean();
        queryBean.setTableName("table");
        DimensionBean bean = new DimensionBean();
        bean.setType(DimensionType.DETAIL_ALL_COLUMN);
        queryBean.setDimensions(Arrays.asList(bean));
        info.setAuthCode("authCode");
        info.setDatabase(SwiftDatabase.CUBE);
        info.setQueryJson(QueryBeanFactory.queryBean2String(queryBean));

        String json = JsonBuilder.writeJsonString(info);
        QueryRequestInfo newInfo = JsonBuilder.readValue(json, QueryRequestInfo.class);
        assertEquals(info.getDatabase(), newInfo.getDatabase());
        assertEquals(info.getAuthCode(), newInfo.getAuthCode());
        String real = newInfo.getQueryJson();
        QueryInfoBean queryInfoBean = QueryBeanFactory.create(real);
        assertTrue(queryInfoBean instanceof DetailQueryInfoBean);
        List<DimensionBean> dimensionBeans = ((DetailQueryInfoBean) queryInfoBean).getDimensions();
        assertEquals(1, dimensionBeans.size());
        DimensionType type = dimensionBeans.get(0).getType();
        assertEquals(DimensionType.DETAIL_ALL_COLUMN, type);
    }

    @Test
    public void tableRequestInfoTest() throws Exception {
        TableRequestInfo info = new TableRequestInfo(ApiRequestType.TRUNCATE_TABLE);
        info.setTable("table");
        info.setAuthCode("authCode");
        info.setDatabase(SwiftDatabase.CUBE);
        String json = JsonBuilder.writeJsonString(info);
        TableRequestInfo newInfo = JsonBuilder.readValue(json, TableRequestInfo.class);
        assertEquals(info.getTable(), newInfo.getTable());
        assertEquals(info.getDatabase(), newInfo.getDatabase());
        assertEquals(info.getAuthCode(), newInfo.getAuthCode());
        assertEquals(ApiRequestType.TRUNCATE_TABLE, newInfo.getRequest());
    }

}