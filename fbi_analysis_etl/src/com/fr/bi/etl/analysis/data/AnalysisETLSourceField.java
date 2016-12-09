package com.fr.bi.etl.analysis.data;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;

import java.io.Serializable;

/**
 * Created by 小灰灰 on 2016/5/12.
 */
public class AnalysisETLSourceField implements JSONTransform,Serializable{
    private static final long serialVersionUID = 4134942806350474094L;
    private String fieldName;
    private int fieldType;
    private int group = -1;
    private String id;
    private String uid ;

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("field_name", fieldName);
        jo.put("field_type", fieldType);
        if (group != -1){
            jo.put("group", group);
        }
        if (!StringUtils.isEmpty(id)){
            jo.put("id", id);
        }
        if (!StringUtils.isEmpty(uid)){
            jo.put("uid", uid);
        }
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.fieldName = jo.getString("field_name");
        this.fieldType = jo.getInt("field_type");
        if (jo.has("group")){
            try{
                this.group = jo.getInt("group");
            } catch (Exception e){
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
        if (jo.has("id")){
            this.id = jo.getString("id");
        }
        if (jo.has("uid")){
            this.uid = jo.getString("uid");
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getFieldType() {
        return fieldType;
    }

    @Override
    public String toString() {
        return "AnalysisETLSourceField{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldType=" + fieldType +
                ", group=" + group +
                ", id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
