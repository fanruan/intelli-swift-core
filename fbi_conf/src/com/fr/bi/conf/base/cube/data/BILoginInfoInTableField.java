package com.fr.bi.conf.base.cube.data;

import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by Young's on 2016/5/19.
 */
public class BILoginInfoInTableField implements JSONTransform {
    private ITableSource table;
    private String fieldName;

    public ITableSource getTable() {
        return table;
    }

    public void setTable(ITableSource table) {
        this.table = table;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if(fieldName != null) {
            jo.put("field_name", fieldName);
        }
        if(table != null) {
            jo.put("table", table.createJSON());
        }
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("field_name")){
            fieldName = jo.getString("field_name");
        }
        if(jo.has("table")) {
            table = TableSourceFactory.createTableSource(jo.getJSONObject("table"), UserControl.getInstance().getSuperManagerID());
        }
    }
}
