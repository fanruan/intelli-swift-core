package com.finebi.cube.relation;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.constant.BIValueConstant;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * BI 表关联
 *
 * @author Daniel-pc
 */
public class BITableRelation extends BIBasicRelation<BusinessTable, BusinessField> implements JSONTransform {
    public BITableRelation() {
    }

    public BITableRelation(BusinessField primaryField, BusinessField foreignField) {
        super(primaryField, foreignField, primaryField.getTableBelongTo(), foreignField.getTableBelongTo());
    }


    public BITableRelation(String keyTableID, String keyFieldName,
                           String foreignKeyTableID, String foreignKeyFieldName) {
        this(BIFactoryHelper.getObject(BusinessField.class, keyTableID, keyFieldName),
                BIFactoryHelper.getObject(BusinessField.class, foreignKeyTableID, foreignKeyFieldName));
    }

    BusinessField generateField(String keyTableID, String keyFieldName) {
        return BIFactoryHelper.getObject(BusinessField.class, keyTableID, keyFieldName);
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("primaryKey")) {
            this.primaryField = generateField(BIValueConstant.EMPTY, BIValueConstant.EMPTY);
            this.primaryField.parseJSON(jo.getJSONObject("primaryKey"));
            this.primaryTable = this.primaryField.getTableBelongTo();
        }
        if (jo.has("foreignKey")) {
            this.foreignField = generateField(BIValueConstant.EMPTY, BIValueConstant.EMPTY);
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

}