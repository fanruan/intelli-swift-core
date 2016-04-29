package com.fr.bi.conf.base.pack.data;

import com.fr.bi.stable.data.BIBasicField;

/**
 * 业务包字段，分析时的字段应该继承该类
 * Created by GUY on 2015/4/10.
 */
public class BIBusinessField extends BIBasicField {
    /**
     *
     */
    private static final long serialVersionUID = 1769507505300033733L;

    public BIBusinessField() {
        super();
    }

    public BIBusinessField(String id, String fieldName, int fieldType, int fieldSize) {
        super(id, fieldName, fieldType, fieldSize);
    }


    /**
     * Connery:所有不满足createJSON要求的都删除。{@code BIJSONObject}
     * @param jo json对象
     * @throws Exception
     */
    /*public JSONObject createJSON(CubeTILoader loader) throws Exception {
        JSONObject jo = createJSON();
        TableIndex ti = loader.getTableIndex(getTableBelongTo());
        if (getFieldType() == DBConstant.COLUMN.NUMBER) {
            jo.put(BIJSONConstant.JSON_KEYS.FILED_MAX_VALUE, ti != null ? ti.getMAXValue(loader.getFieldIndex(this)) : 0);
            jo.put(BIJSONConstant.JSON_KEYS.FIELD_MIN_VALUE, ti != null ? ti.getMINValue(loader.getFieldIndex(this)) : 0);

        }
        return jo;
    }
*/

}