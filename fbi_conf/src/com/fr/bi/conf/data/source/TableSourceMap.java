package com.fr.bi.conf.data.source;

import com.fr.bi.stable.constant.DBConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Young's on 2016/7/5.
 */
public class TableSourceMap {
    public static final Map<String, Class> SOURCES = new HashMap<String, Class>() {
        {
            put(DBConstant.CONNECTION.ETL_CONNECTION, ETLTableSource.class);
            put(DBConstant.CONNECTION.EXCEL_CONNECTION, ExcelTableSource.class);
            put(DBConstant.CONNECTION.SERVER_CONNECTION, ServerTableSource.class);
            put(DBConstant.CONNECTION.SQL_CONNECTION, SQLTableSource.class);
        }
    };
}
