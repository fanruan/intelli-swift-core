package com.fr.swift.jdbc.info;

import com.fr.swift.base.json.JsonBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2019-01-11
 */
public class JdbcRequestInfoTest {

    @Test
    public void testColumnsRequest() throws Exception {
        ColumnsRequestInfo columnsRequestInfo = new ColumnsRequestInfo("cube", "table", "authCode");
        String jsonString = JsonBuilder.writeJsonString(columnsRequestInfo);
        ColumnsRequestInfo newInfo = JsonBuilder.readValue(jsonString, ColumnsRequestInfo.class);
        assertEquals("table", newInfo.getTable());
        assertEquals("cube", newInfo.getDatabase());
        assertEquals("authCode", newInfo.getAuthCode());
    }

    @Test
    public void testSqlRequest() throws Exception {
        SqlRequestInfo requestInfo = new SqlRequestInfo("select * from tableA", true);
        requestInfo.setDatabase("cube");
        requestInfo.setAuthCode("authCode");
        String jsonString = JsonBuilder.writeJsonString(requestInfo);
        SqlRequestInfo newInfo = JsonBuilder.readValue(jsonString, SqlRequestInfo.class);
        assertEquals("select * from tableA", newInfo.getSql());
        assertEquals("cube", newInfo.getDatabase());
        assertEquals("authCode", newInfo.getAuthCode());
    }

    @Test
    public void testTablesRequest() throws Exception {
        TablesRequestInfo info = new TablesRequestInfo("cube", "authCode");
        String jsonString = JsonBuilder.writeJsonString(info);
        TablesRequestInfo newInfo = JsonBuilder.readValue(jsonString, TablesRequestInfo.class);
        assertEquals("cube", newInfo.getDatabase());
        assertEquals("authCode", newInfo.getAuthCode());
    }
}