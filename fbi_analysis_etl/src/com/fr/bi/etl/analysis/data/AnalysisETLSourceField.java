package com.fr.bi.etl.analysis.data;

import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by 小灰灰 on 2016/5/12.
 */
public class AnalysisETLSourceField implements JSONTransform{
    private String fieldName;
    private int fieldType;
    private int group = -1;
    private int id = -1;
    private int uid = -1;

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("field_name", fieldName);
        jo.put("field_type", fieldType);
        if (group != -1){
            jo.put("group", group);
        }
        if (id != -1){
            jo.put("id", id);
        }
        if (uid != -1){
            jo.put("uid", uid);
        }
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.fieldName = jo.getString("field_name");
        this.fieldType = jo.getInt("field_type");
        if (jo.has("group")){
            this.group = jo.getInt("group");
        }
        if (jo.has("id")){
            this.id = jo.getInt("id");
        }
        if (jo.has("uid")){
            this.uid = jo.getInt("uid");
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getFieldType() {
        return fieldType;
    }
}
