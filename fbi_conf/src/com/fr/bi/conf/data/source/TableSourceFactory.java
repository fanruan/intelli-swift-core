package com.fr.bi.conf.data.source;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/12/21.
 */
public class TableSourceFactory {

    public static final Map<String, Class> SOURCES = new HashMap<String, Class>() {
        {
            put(DBConstant.CONNECTION.ETL_CONNECTION, ETLTableSource.class);
            put(DBConstant.CONNECTION.EXCEL_CONNECTION, ExcelTableSource.class);
            put(DBConstant.CONNECTION.SERVER_CONNECTION, ServerTableSource.class);
            put(DBConstant.CONNECTION.SQL_CONNECTION, SQLTableSource.class);
        }
    };

    public static AbstractTableSource createTableSource(JSONObject jo, long userId) throws Exception {
        String connectionName = jo.optString("connection_name", StringUtils.EMPTY);
        AbstractTableSource tableSource;
        if(SOURCES.get(connectionName) != null) {
            tableSource = (AbstractTableSource) SOURCES.get(connectionName).newInstance();
            tableSource.parseJSON(jo, userId);
        } else {
            tableSource = new DBTableSource();
            tableSource.parseJSON(jo, userId);
        }
        return tableSource;
    }
}