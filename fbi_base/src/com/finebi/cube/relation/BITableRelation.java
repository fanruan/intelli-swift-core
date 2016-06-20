package com.finebi.cube.relation;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
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

//    BusinessField generateField(String keyTableID, String keyFieldName) {
//        return BIFactoryHelper.getObject(BusinessField.class, keyTableID, keyFieldName);
//    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        throw new UnsupportedOperationException("parseJson禁用");
    }

    /**
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject primaryJson = this.primaryField.createJSON();
        primaryJson.put("field_id", this.primaryField.getFieldID().getIdentityValue());
        JSONObject foreignJson = this.foreignField.createJSON();
        foreignJson.put("field_id", this.foreignField.getFieldID().getIdentityValue());
        jo.put("primaryKey", primaryJson);
        jo.put("foreignKey", foreignJson);
        return jo;
    }


}