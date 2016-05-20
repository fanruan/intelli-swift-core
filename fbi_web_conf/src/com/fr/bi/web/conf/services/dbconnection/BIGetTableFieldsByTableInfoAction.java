package com.fr.bi.web.conf.services.dbconnection;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2015/9/25.
 *
 */
public class BIGetTableFieldsByTableInfoAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String connName = WebUtils.getHTTPRequestParameter(req, "connection_name");
        String tableName = WebUtils.getHTTPRequestParameter(req, "table_name");
        PersistentTable persistentTable = BIDBUtils.getDBTable(connName, tableName);
        JSONObject table = persistentTable.createJSON();
        JSONArray fields = this.parseFields(table.getJSONArray("fields"));
        JSONArray stringField = new JSONArray();
        JSONArray numField = new JSONArray();
        JSONArray dateField = new JSONArray();
        for(int i = 0; i < fields.length(); i++){
            JSONObject field = fields.getJSONObject(i);

            switch (field.optInt("field_type")){
                case DBConstant.COLUMN.STRING:
                    stringField.put(field);
                    break;
                case DBConstant.COLUMN.NUMBER:
                    numField.put(field);
                    break;
                case DBConstant.COLUMN.DATE:
                    dateField.put(field);
                    break;
            }
        }
        JSONArray newFields = new JSONArray();
        newFields.put(stringField).put(numField).put(dateField);
        WebUtils.printAsJSON(res, newFields);
    }

    @Override
    public String getCMD() {
        return "get_table_field_by_table_info";
    }

    public JSONArray parseFields(JSONArray fields) throws Exception{
        JSONArray newFields = new JSONArray();
        for(int i = 0; i < fields.length(); i++){
            JSONObject field = fields.getJSONObject(i);
            JSONObject newField = new JSONObject();
            newField.put("field_name", field.optString("value"))
                    .put("field_type", field.optString("biColumnType"))
                    .put("field_size", field.optString("column_size"))
                    .put("is_usable", true);
            newFields.put(newField);
        }
        return newFields;
    }
}