package com.finebi.cube.conf.field;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

/**
 * This class created on 2016/5/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBusinessFieldWrapper implements BusinessField {
    private BusinessField field;

    public BIBusinessFieldWrapper(BusinessField field) {
        BINonValueUtils.checkNull(field);
        if (field.getFieldID() != null && !(field instanceof BIBusinessFieldWrapper)) {
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
