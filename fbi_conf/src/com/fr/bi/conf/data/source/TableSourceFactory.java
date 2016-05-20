package com.fr.bi.conf.data.source;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/12/21.
 */
public class TableSourceFactory {
    public static AbstractTableSource createTableSource(JSONObject jo, long userId) throws Exception {
        String connectionName = jo.optString("connection_name", StringUtils.EMPTY);
        if (ComparatorUtils.equals(connectionName, DBConstant.CONNECTION.ETL_CONNECTION)) {
            ETLTableSource etlTableSource = new ETLTableSource();
            etlTableSource.parseJSON(jo, userId);
            JSONArray tables = jo.optJSONArray("tables");
            List<ICubeTableSource> parents = new ArrayList<ICubeTableSource>();
            for (int i = 0; i < tables.length(); i++) {
                parents.add(createTableSource(tables.getJSONObject(i), userId));
            }
            etlTableSource.setParents(parents);
            return etlTableSource;
        } else if (ComparatorUtils.equals(connectionName, DBConstant.CONNECTION.SQL_CONNECTION)) {
            SQLTableSource sqlTableSource = new SQLTableSource();
            sqlTableSource.parseJSON(jo, userId);
            return sqlTableSource;
        } else if (ComparatorUtils.equals(connectionName, DBConstant.CONNECTION.EXCEL_CONNECTION)) {
            ExcelTableSource excelTableSource = new ExcelTableSource();
            excelTableSource.parseJSON(jo);
            return excelTableSource;
        } else {
            String tableName = jo.optString("table_name", StringUtils.EMPTY);
            return new DBTableSource(connectionName, tableName);
        }
    }
}