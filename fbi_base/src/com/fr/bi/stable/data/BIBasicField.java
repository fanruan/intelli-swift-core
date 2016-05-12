package com.fr.bi.stable.data;

import com.fr.bi.common.constant.BIValueConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.io.Serializable;

/**
 * Created by Connery on 2015/12/22.
 */
public class BIBasicField extends BIField implements JSONTransform, Cloneable, Serializable, XMLable {

    private static final long serialVersionUID = 3249424696123813392L;


    protected int fieldType;
    protected int fieldSize;
    protected boolean isUsable;
    private boolean canSetUseable = true;

    public BIBasicField() {
        super();
    }

    public BIBasicField(String id, String fieldName, int classType, int fieldSize) {
        this(id, fieldName, BIDBUtils.checkColumnTypeFromClass(classType), fieldSize, true);
    }

    public BIBasicField(String id, String fieldName) {
        super(id, fieldName);
    }

    private BIBasicField(String id, String fieldName, int fieldType, int fieldSize, boolean isUsable) {
        super(id, fieldName);
        this.fieldType = fieldType;
        this.fieldSize = fieldSize;
        this.isUsable = isUsable;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BIBasicField newOne = null;
        newOne = (BIBasicField) super.clone();
        newOne.fieldSize = this.fieldSize;
        newOne.fieldType = this.fieldType;
        newOne.isUsable = this.isUsable;
        return newOne;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    public boolean isUsable() {
        return isUsable;
    }

    public void setUsable(boolean isUsable) {
        this.isUsable = isUsable;
    }

    /**
     * 转成JSON
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
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
        JSONObject jo = super.createJSON();
        jo.put("field_type", fieldType)
                .put("field_size", fieldSize)
                .put("is_usable", isUsable)
                .put("is_enable", canSetUseable);
        return jo;
    }
    public JSONObject createJSON(ICubeDataLoader loader) throws Exception {
        JSONObject jo = createJSON();
        if (getFieldType() ==  DBConstant.COLUMN.NUMBER){
            ICubeTableService ti = null;
            try{
                 ti = loader.getTableIndex(this);
            } catch (Exception e){

            }
            jo.put(BIJSONConstant.JSON_KEYS.FILED_MAX_VALUE, ti != null ? ti.getMAXValue(loader.getFieldIndex(this)) : 0);
            jo.put(BIJSONConstant.JSON_KEYS.FIELD_MIN_VALUE, ti != null ? ti.getMINValue(loader.getFieldIndex(this)) : 0 );
        }
        return jo;
    }
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(super.XML_TAG);
        writer.attr("field_name", fieldName);
        writer.attr("field_type", fieldType)
                .attr("field_size", fieldSize)
                .attr("is_usable", isUsable).attr("id", tableBelongTo.getID().getIdentityValue());
        writer.end();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.tableBelongTo = new BITable(new BITableID(reader.getAttrAsString("id", BIValueConstant.EMPTY)));
            this.fieldName = reader.getAttrAsString("field_name", StringUtils.EMPTY);
            fieldType = reader.getAttrAsInt("field_type", 0);
            fieldSize = reader.getAttrAsInt("field_size", 0);
            isUsable = reader.getAttrAsBoolean("is_usable", true);
        }
    }

    public void setCanSetUseable(boolean canSetUseable) {
        this.canSetUseable = canSetUseable;
    }
}