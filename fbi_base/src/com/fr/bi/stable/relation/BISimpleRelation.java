package com.fr.bi.stable.relation;

import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;


public class BISimpleRelation {

    private String primaryId;
    private String foreignId;

    public BISimpleRelation() {

    }

    public BISimpleRelation(String primaryId, String foreignId) {
        this.primaryId = primaryId;
        this.foreignId = foreignId;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public String getForeignId() {
        return foreignId;
    }

    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("primaryKey")){
            JSONObject key = jo.optJSONObject("primaryKey");
            primaryId = key.optString("field_id");
        }
        if(jo.has("foreignKey")){
            JSONObject key = jo.optJSONObject("foreignKey");
            foreignId = key.optString("field_id");
        }
    }

    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("primaryKey", new JSONObject().put("field_id", primaryId));
        jo.put("foreignKey", new JSONObject().put("field_id", foreignId));
        return  jo;
    }

    public BITableRelation getTableRelation(){
        BITableRelation relation = new BITableRelation();
        JSONObject jo = new JSONObject();
        try{
            jo.put("primaryKey", new JSONObject().put("table_id", BIIDUtils.getTableIDFromFieldID(primaryId)).put("field_name", BIIDUtils.getFieldNameFromFieldID(primaryId)));
            jo.put("foreignKey", new JSONObject().put("table_id", BIIDUtils.getTableIDFromFieldID(foreignId)).put("field_name", BIIDUtils.getFieldNameFromFieldID(foreignId)));
            relation.parseJSON(jo);
        }catch(Exception e){
            BILogger.logger.error(e.getMessage());
        }
        return relation;
    }
}
