package com.finebi.cube.conf.relation;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.field.BusinessFieldHelper;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

/**
 * Created by roy on 16/5/27.
 */
public class BITableRelationHelper {
    public static BITableRelation getRelation(JSONObject relationJson){
        try {
            JSONObject primaryJson = relationJson.getJSONObject("primaryKey");
            JSONObject foreignJson = relationJson.getJSONObject("foreignKey");
            String primaryFieldID = primaryJson.getString("field_id");
            String foreignFieldID = foreignJson.getString("field_id");
            BusinessField primaryField = BusinessFieldHelper.getBusinessFieldSource(new BIFieldID(primaryFieldID));
            BusinessField foreignField = BusinessFieldHelper.getBusinessFieldSource(new BIFieldID(foreignFieldID));
          return   new BITableRelation(primaryField,foreignField);
        }catch (Exception e){
            throw BINonValueUtils.beyondControl(e);
        }

    }
}
