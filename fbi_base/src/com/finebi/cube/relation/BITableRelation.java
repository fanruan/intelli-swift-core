package com.finebi.cube.relation;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;
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

    /**
     * @param o
     * @return 这个地方很恶心, etl-join有可能会导致tableRelation没变,但是sourceRelation改变,之后需要把sourceRelation提出来单独一个manager来解藕
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof BITableRelation)) {
            return false;
        }
        BITableRelation that = (BITableRelation) o;

        if (primaryTable.getTableSource() != null ? !ComparatorUtils.equals(primaryTable.getTableSource(), that.primaryTable.getTableSource()) : that.primaryTable.getTableSource() != null) {
            return false;
        }
        return !(foreignTable.getTableSource() != null ? !ComparatorUtils.equals(foreignTable.getTableSource(), that.foreignTable.getTableSource()) : that.foreignTable.getTableSource() != null);


    }
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof BITableRelation)) {
//            return false;
//        }
//        BITableRelation o1 = (BITableRelation) o;
//        return o1==this;
//    }
//
//    @Override
//    public int hashCode() {
//        try {
//            return this.createJSON().hashCode();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }


}
