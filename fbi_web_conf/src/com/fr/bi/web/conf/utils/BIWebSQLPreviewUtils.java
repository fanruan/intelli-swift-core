package com.fr.bi.web.conf.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.SQLStatement;
import com.fr.data.impl.DBTableData;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.file.DatasourceManager;
import com.fr.general.data.DataModel;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by kary on 2016/11/1.
 */
public class BIWebSQLPreviewUtils {
    public static JSONObject getPreviewData(String sql, String connectionName) throws JSONException {
        com.fr.data.impl.Connection dbc = DatasourceManager.getInstance().getConnection(connectionName);
        DBTableData dbTableData = new DBTableData(dbc, sql);
        JSONObject jo = new JSONObject();
        try {
            DataModel dm;
            //转换成内置数据集
            EmbeddedTableData emTableData = EmbeddedTableData.embedify(dbTableData, null, BIBaseConstant.PREVIEW_COUNT);
            dm = emTableData.createDataModel(null);
            int cols = dm.getColumnCount();
            int rows = dm.getRowCount();
            JSONArray fieldNameArray = new JSONArray();
            JSONArray dataArray = new JSONArray();
            for (int i = 0; i < cols; i++) {
                fieldNameArray.put(dm.getColumnName(i));
            }
            for (int i = 0; i < rows; i++) {
                JSONArray oneRow = new JSONArray();
                for (int j = 0; j < cols; j++) {
                    oneRow.put(dm.getValueAt(i, j));
                }
                dataArray.put(oneRow);
            }

            jo.put("field_names", fieldNameArray);
            jo.put("data", dataArray);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
            jo.put("error", e.getMessage());
        }
        return jo;
    }
    public static String getTableQuery(String subQuery){
        SQLStatement sqlStatement = new SQLStatement(null);
        sqlStatement.setFrom("(\n" + subQuery + "\n) " + "t");
        return sqlStatement.toString();
    }
}
