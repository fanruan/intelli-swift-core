package com.fr.bi.web.conf.services.dbconnection;

import com.fr.bi.conf.data.source.*;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2015/12/3.
 * 根据表的信息获取表字段信息——对于新添加的表
 */
public class BIGetFieldInfo4NewTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tables = WebUtils.getHTTPRequestParameter(req, "tables");
        JSONArray tablesJA = new JSONArray(tables);
        JSONArray tablesWithFields = new JSONArray();
        for(int i = 0; i < tablesJA.length(); i++){
            JSONObject table = tablesJA.getJSONObject(i);
            CubeTableSource source = TableSourceFactory.createTableSource(table, userId);
            JSONObject data = source.createJSON();
            if(table.has("id")){
                formatTableDataFields(table.getString("id"), data);
            }
            tablesWithFields.put(data);
        }
        WebUtils.printAsJSON(res, tablesWithFields);
    }

    @Override
    public String getCMD() {
        return "get_field_info_4_new_tables";
    }

    /**
     * 最外层的fields信息修改
     * @param tableId
     * @param tableData
     * @throws Exception
     */
    private void formatTableDataFields(String tableId, JSONObject tableData) throws Exception{
        JSONArray fields = tableData.getJSONArray("fields");
        JSONArray newFields = new JSONArray();
        for(int i = 0; i < fields.length(); i++){
            JSONArray fs = fields.getJSONArray(i);
            JSONArray nFields = new JSONArray();
            for(int j = 0; j < fs.length(); j++){
                JSONObject field = fs.getJSONObject(j);
                field.put("id", tableId + field.getString("field_name"));
                field.put("table_id", tableId);
                nFields.put(field);
            }
            newFields.put(nFields);
        }
        tableData.put("fields", newFields);
    }
}