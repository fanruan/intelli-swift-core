package com.fr.bi.stable.data.db;

import com.finebi.common.name.Name;
import com.finebi.common.name.NameImp;
import com.finebi.common.resource.ResourceName;
import com.finebi.common.resource.ResourceNameImpl;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.data.tableSource.FineCubeTable;
import com.fr.bi.stable.utils.BIDBUtils;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

/**
 * Created by Roy on 2017/6/16.
 */
public class BIFineCubeField implements FineCubeField {
    private int classType;
    protected int fieldType;
    protected int fieldSize;
    protected ResourceName fieldName;
    protected FineCubeTable tableBelongTo;
    protected boolean usable = true;
    private boolean canSetUsable = true;


    public BIFineCubeField(FineCubeTable tableBelongTo, String fieldName, int classType, int fieldSize) {
        this.tableBelongTo = tableBelongTo;
        this.fieldName = new ResourceNameImpl(new NameImp(fieldName, tableBelongTo));
        this.fieldType = BIDBUtils.checkColumnTypeFromClass(classType);
        this.fieldSize = fieldSize;
        this.classType = classType;
    }

    @Override
    public String getFieldName() {
        return fieldName.value();
    }

    @Override
    public int getFieldType() {
        return fieldType;
    }

    @Override
    public void setTableBelongTo(CubeTableSource tableBelongTo) {
        this.tableBelongTo = (FineCubeTable) tableBelongTo;
    }


    public FineCubeTable getTableBelongTo() {
        return tableBelongTo;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    /**
     * 返回字段对应的java类
     *
     * @return
     */
    @Override
    public int getClassType() {
        return classType;
    }

    public boolean isUsable() {
        return usable;
    }

    @Override
    public boolean hasSubField() {
        return (getFieldType() == DBConstant.COLUMN.DATE);
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = new ResourceNameImpl(new NameImp(fieldName, tableBelongTo));

    }

    /**
     * 转成JSON
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("fieldName")) {
            this.setFieldName(jo.getString("fieldName"));
        }
        if (jo.has("fieldType")) {
            fieldType = jo.optInt("fieldType", 0);
        }
        if (jo.has("fieldSize")) {
            fieldSize = jo.optInt("fieldSize", 0);
        }
        if (jo.has("isUsable")) {
            usable = jo.optBoolean("isUsable", true);
        }

        if (jo.has("isEnable")) {
            canSetUsable = jo.optBoolean("isEnable", true);
        }
        if (jo.has("classType")) {
            classType = jo.getInt("classType");
        }
        //兼容之前的cube
        if (jo.has("field_name")) {

            this.setFieldName(jo.getString("field_name"));
        }
        if (jo.has("field_type")) {
            fieldType = jo.optInt("field_type", 0);
        }
        if (jo.has("field_size")) {
            fieldSize = jo.optInt("field_size", 0);
        }
        if (jo.has("is_usable")) {
            usable = jo.optBoolean("is_usable", true);
        }

        if (jo.has("is_enable")) {
            canSetUsable = jo.optBoolean("is_enable", true);
        }
        if (jo.has("class_type")) {
            classType = jo.getInt("class_type");
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
        jo.put("fieldName", getFieldName());
        jo.put("fieldType", fieldType)
                .put("fieldSize", fieldSize)
                .put("isUsable", isUsable())
                .put("isEnable", canSetUsable)
                .put("classType", classType);

        return jo;
    }


    @Override
    public Name getName() {
        return fieldName;
    }

    @Override
    public ResourceName getResourceName() {
        return fieldName;
    }

    @Override
    public String getFieldUniqueName() {
        return fieldName.uniqueValue();
    }

    public boolean canSetUsable() {
        return canSetUsable;
    }

    @Override
    public void setCanSetUsable(boolean canSetUsable) {
        this.canSetUsable = canSetUsable;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        BIFineCubeField cloned = (BIFineCubeField) super.clone();
        if (fieldName != null) {
            cloned.fieldName = fieldName;
        }
        if (tableBelongTo != null) {
            cloned.tableBelongTo = (FineCubeTable) tableBelongTo.clone();
        }
        return cloned;
    }

    @Override
    public long version() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BIFineCubeField
                && ComparatorUtils.equals(fieldName, ((BIFineCubeField) o).fieldName)
                && ComparatorUtils.equals(tableBelongTo, ((BIFineCubeField) o).tableBelongTo);
    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (tableBelongTo != null ? tableBelongTo.hashCode() : 0);
        return result;
    }

}
