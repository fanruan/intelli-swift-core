package com.fr.bi.stable.relation;

import com.fr.bi.common.constant.BIValueConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * BI 表关联
 *
 * @author Daniel-pc
 */
public class BITableRelation extends BIBasicRelation<Table, BIField> implements JSONTransform, Relation {
    public BITableRelation() {
    }

    public BITableRelation(BIField primaryField, BIField foreignField) {
        super(primaryField, foreignField, primaryField.getTableBelongTo(), foreignField.getTableBelongTo());
    }


    public BITableRelation(String keyTableID, String keyFieldName,
                           String foreignKeyTableID, String foreignKeyFieldName) {
        this(new BIField(keyTableID, keyFieldName), new BIField(foreignKeyTableID, foreignKeyFieldName));
    }


    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("primaryKey")) {
            this.primaryField = new BIField(BIValueConstant.EMPTY, BIValueConstant.EMPTY);
            this.primaryField.parseJSON(jo.getJSONObject("primaryKey"));
            this.primaryTable = this.primaryField.getTableBelongTo();
        }
        if (jo.has("foreignKey")) {
            this.foreignField = new BIField(BIValueConstant.EMPTY, BIValueConstant.EMPTY);
            this.foreignField.parseJSON(jo.getJSONObject("foreignKey"));
            this.foreignTable = foreignField.getTableBelongTo();
        }
    }

    /**
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("primaryKey", this.primaryField.createJSON());
        jo.put("foreignKey", this.foreignField.createJSON());
        return jo;
    }

    public BISimpleRelation getSimpleRelation() throws Exception {
        return new BISimpleRelation(BIIDUtils.createFieldID(this.primaryField), BIIDUtils.createFieldID(this.foreignField));
    }
}