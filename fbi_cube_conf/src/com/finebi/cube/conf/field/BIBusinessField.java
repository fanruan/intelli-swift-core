package com.finebi.cube.conf.field;


import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.json.JSONObject;


/**
 * 业务包字段，分析时的字段应该继承该类
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BusinessField.class)
public class BIBusinessField implements BusinessField {
    /**
     *
     */
    private static final long serialVersionUID = 1769507505300033733L;

    protected String fieldName = "";
    protected BIFieldID fieldID;
    /**
     * BI的字段类型，只有数值，日期和字符类型
     */
    protected int fieldType;
    protected int fieldSize;
    /**
     * 字段的实际存储类型。
     * 比如日期类型可能是Long的数据等
     */
    protected int classType;
    protected boolean isUsable;
    private boolean canSetUsable = true;
    protected BusinessTable tableBelongTo;

    /**
     * 字段是否是自循环表展示字段
     */
    protected boolean isCircle;

    public BIBusinessField(BusinessTable tableBelongTo, BIFieldID fieldID, String fieldName, int classType, int fieldSize, boolean isUsable, boolean canSetUsable, boolean isCircle) {
        this.tableBelongTo = tableBelongTo;
        this.fieldName = fieldName;
        this.fieldID = fieldID;
        this.fieldType = BIDBUtils.checkColumnTypeFromClass(classType);
        this.fieldSize = fieldSize;
        this.classType = classType;
        this.isUsable = isUsable;
        this.canSetUsable = canSetUsable;
        this.isCircle = isCircle;
    }

    public BIBusinessField(BusinessTable tableBelongTo, BIFieldID fieldID, String fieldName, int classType, int fieldSize, boolean isUsable, boolean canSetUsable) {
        this(tableBelongTo, fieldID, fieldName, classType, fieldSize, isUsable, canSetUsable, false);
    }

    public BIBusinessField(String tableID, String fieldName) {
        this(new BIBusinessTable(new BITableID(tableID)), fieldName);
    }

    public BIBusinessField(BusinessTable tableBelongTo, BIFieldID fieldID, String fieldName, int classType, int fieldSize) {
        this(tableBelongTo, fieldID, fieldName, classType, fieldSize, true, true, false);
    }

    public BIBusinessField(String tableID, String fieldName, BIFieldID fieldID) {
        this(new BIBusinessTable(new BITableID(tableID)), fieldName, fieldID);
    }

    public BIBusinessField() {

    }

    @Override
    public BIFieldID getFieldID() {
        return fieldID;
    }

    public BIBusinessField(BIFieldID fieldID) {
        this.fieldID = fieldID;
    }

    public int getClassType() {
        return classType;
    }

    public BIBusinessField(BusinessTable tableBelongTo, String fieldName) {
        this(tableBelongTo, new BIFieldID(""), fieldName, 0, 0, true, true, false);
    }

    public BIBusinessField(BusinessTable tableBelongTo, String fieldName, BIFieldID fieldId) {
        this(tableBelongTo, fieldId, fieldName, 0, 0, true, true, false);
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void setTableBelongTo(BusinessTable tableBelongTo) {
        this.tableBelongTo = tableBelongTo;
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
    public BusinessTable getTableBelongTo() {
        return tableBelongTo;
    }

    public boolean isCircle(){
        return isCircle;
    }

    public void setCircle(boolean isCircle){
        this.isCircle = isCircle;
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
        if (jo.has("id")) {
            String fieldId = jo.getString("id");
            this.fieldID = new BIFieldID(fieldId);
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
        if (jo.has("is_enable")) {
            canSetUsable = jo.optBoolean("is_enable", true);
        }
        if (jo.has("isCircle")){
            isCircle = jo.optBoolean("isCircle", false);
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
        jo.put("id", fieldID.getIdentityValue());
        jo.put("table_id", getTableBelongTo().getID().getIdentityValue());
        jo.put("field_type", fieldType)
                .put("field_size", fieldSize)
                .put("is_usable", isUsable)
                .put("is_enable", canSetUsable)
                .put("isCircle", isCircle);
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

    @Override
    public String toString() {
        return "BIBusinessField{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldID=" + fieldID +
                ", fieldType=" + fieldType +
                ", fieldSize=" + fieldSize +
                ", classType=" + classType +
                ", tableBelongTo=" + tableBelongTo +
                '}';
    }
}