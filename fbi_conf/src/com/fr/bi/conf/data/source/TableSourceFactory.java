package com.fr.bi.conf.data.source;

import com.fr.bi.conf.data.source.operator.OperatorFactory;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
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
            JSONArray tables = jo.optJSONArray("tables");
            List<CubeTableSource> parents = new ArrayList<CubeTableSource>();
            for (int i = 0; i < tables.length(); i++) {
                parents.add(createTableSource(tables.getJSONObject(i), userId));
            }
            ETLTableSource etlTableSource = new ETLTableSource(OperatorFactory.createOperatorsByJSON(jo, userId), parents);
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