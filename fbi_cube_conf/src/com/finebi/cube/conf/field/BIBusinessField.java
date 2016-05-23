package com.finebi.cube.conf.field;


import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.json.JSONObject;


/**
 * 业务包字段，分析时的字段应该继承该类
 * Created by GUY on 2015/4/10.
 */
public class BIBusinessField implements IBusinessField {
    /**
     *
     */
    private static final long serialVersionUID = 1769507505300033733L;

    protected String fieldName = "";
    protected String fieldID;
    protected int fieldType;
    protected int fieldSize;
    protected boolean isUsable;
    private boolean canSetUsable = true;
    protected IBusinessTable tableBelongTo;

    public BIBusinessField(IBusinessTable tableBelongTo, String fieldName, int classType, int fieldSize) {
        this.tableBelongTo = tableBelongTo;
        this.fieldName = fieldName;
        this.fieldType = BIDBUtils.checkColumnTypeFromClass(classType);
        this.fieldSize = fieldSize;
    }

    public BIBusinessField(String tableID, String fieldName) {
        this(new BIBusinessTable(new BITableID(tableID)), fieldName);
    }

    public BIBusinessField(IBusinessTable tableBelongTo, String fieldName) {
        this(tableBelongTo, fieldName, 0, 0);
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public int getFieldType() {
        return fieldType;
    }

    @Override
    public int getFieldSize() {
        return fieldSize;
    }

    @Override
    public boolean isUsable() {
        return isUsable;
    }

    @Override
    public boolean isCanSetUsable() {
        return canSetUsable;
    }

    @Override
    public IBusinessTable getTableBelongTo() {
        return tableBelongTo;
    }

    /**
     * 转成JSON
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("field_name")) {
            this.setFieldName(jo.getString("field_name"));
        }
        if (jo.has("table_id")) {
            String tableId = jo.getString("table_id");
            tableBelongTo = new BIBusinessTable(new BITableID(tableId));
        }
        if (jo.has("field_id")) {
            String fieldId = jo.getString("field_id");
            this.setFieldName(BIIDUtils.getFieldNameFromFieldID(fieldId));
        }
        if (jo.has("field_type")) {
            fieldType = jo.optInt("field_type", 0);
        }
        if (jo.has("field_size")) {
            fieldSize = jo.optInt("field_size", 0);
        }
        if (jo.has("is_usable")) {
            isUsable = jo.optBoolean("is_usable", true);
        }
    }

    /**
     * 创建JSON
     *
     * @return JSON对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("field_name", getFieldName());
        jo.put("id", fieldID);
        jo.put("table_id", getTableBelongTo().getID().getIdentityValue());
        jo.put("field_type", fieldType)
                .put("field_size", fieldSize)
                .put("is_usable", isUsable)
                .put("is_enable", canSetUsable);
        return jo;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIBusinessField)) return false;

        BIBusinessField that = (BIBusinessField) o;

        return !(fieldID != null ? !fieldID.equals(that.fieldID) : that.fieldID != null);

    }

    @Override
    public int hashCode() {
        return fieldID != null ? fieldID.hashCode() : 0;
    }
}