package com.fr.bi.etl.analysis.data;

import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by 小灰灰 on 2016/5/12.
 */
public class AnalysisETLSourceField implements JSONTransform{
    private String fieldName;
    private int fieldType;

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("field_name", fieldName);
        jo.put("field_type", fieldType);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.fieldName = jo.getString("field_name");
        this.fieldType = jo.getInt("field_type");
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getFieldType() {
        return fieldType;
    }
}
