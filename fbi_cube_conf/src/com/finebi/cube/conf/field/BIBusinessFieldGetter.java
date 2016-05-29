package com.finebi.cube.conf.field;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

/**
 * 工具类，在DataSource记录下来Id和BusinessField对应TableSource
 * 的情况下，通过Helper类实现只有FieldId的情况下，来获得table，Field其他属性。
 * <p/>
 * 使用方法：必须提供一个带Id的BusinessTable实现，并且此BusinessTable必须不是
 * 一个BIBusinessTableGetter。
 * 此外只读不可写
 * This class created on 2016/5/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBusinessFieldGetter implements BusinessField {
    public BIBusinessFieldGetter() {
    }

    private BusinessField field;

    public BIBusinessFieldGetter(BusinessField field) {
        BINonValueUtils.checkNull(field);
        if (field.getFieldID() != null && !(field instanceof BIBusinessFieldGetter)) {
            this.field = BusinessFieldHelper.getBusinessFieldSource(field.getFieldID());
        } else {
            throw BINonValueUtils.beyondControl();
        }
    }

    @Override
    public String getFieldName() {
        return this.field.getFieldName();
    }

    @Override
    public int getFieldType() {
        return field.getClassType();
    }

    @Override
    public int getFieldSize() {
        return field.getFieldSize();
    }

    @Override
    public BIFieldID getFieldID() {
        return field.getFieldID();
    }

    @Override
    public int getClassType() {
        return field.getClassType();
    }

    @Override
    public boolean isUsable() {
        return field.isUsable();
    }

    @Override
    public boolean isCanSetUsable() {
        return field.isCanSetUsable();
    }

    @Override
    public BusinessTable getTableBelongTo() {
        return field.getTableBelongTo();
    }

    @Override
    public JSONObject createJSON(ICubeDataLoader loader) throws Exception {
        return field.createJSON(loader);
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return field.createJSON();
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void setTableBelongTo(BusinessTable tableBelongTo) {
        throw new UnsupportedOperationException();

    }
}
