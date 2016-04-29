package com.fr.bi.stable.data;

import com.fr.bi.common.constant.BIValueConstant;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * BI的Field基本类型。其他Field都继承此类型
 * Field不继承Table，包含所属的Table的ID
 * TODO clone方法
 * Created by Connery on 2015/12/15.
 */
public class BIField implements BIFieldOperation {
    private static final long serialVersionUID = 2624628196039867893L;
    private static BIField BI_EMPTY_FIELD = new BIField(BIValueConstant.EMPTY, BIValueConstant.EMPTY);
    protected String fieldName = StringUtils.EMPTY;
    protected Table tableBelongTo;

    @Override
    public void setTableBelongTo(Table tableBelongTo) {
        this.tableBelongTo = tableBelongTo;
    }

    public BIField() {
        tableBelongTo = BITable.BI_EMPTY_TABLE();
    }

    public BIField(String tableID, String fieldName) {
        this(new BITable(new BITableID(tableID)), fieldName);
    }

    public BIField(Table table, String fieldName) {
        BINonValueUtils.checkNull(table, fieldName);
        this.tableBelongTo = table;
        this.fieldName = fieldName;
    }

    public BIField(BIField biField) {
        this(biField.getTableBelongTo(), biField.getFieldName());
    }


    public BIField(BIFieldID id) {
        this(id.getIdentityValue(), BIValueConstant.EMPTY);
    }

    public static BIField getBiEmptyField() {
        return BI_EMPTY_FIELD;
    }

    @Override
    public Table getTableBelongTo() {
        return tableBelongTo;
    }


    @Override
    public BITableID getTableID() {
        return tableBelongTo.getID();
    }


    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BIField newOne = null;
        newOne = (BIField) super.clone();
        newOne.fieldName = this.fieldName;
        newOne.tableBelongTo = (BITable) this.tableBelongTo.clone();
        return newOne;
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
            tableBelongTo = new BITable(new BITableID(tableId));
        }
        if (jo.has("field_id")) {
            String fieldId = jo.getString("field_id");
            String tableId = BIIDUtils.getTableIDFromFieldID(fieldId);
            tableBelongTo = new BITable(new BITableID(tableId));
            this.setFieldName(BIIDUtils.getFieldNameFromFieldID(fieldId));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIField)) {
            return false;
        }

        BIField biField = (BIField) o;

        if (!ComparatorUtils.equals(fieldName, biField.fieldName)) {
            return false;
        }
        if (!ComparatorUtils.equals(tableBelongTo, biField.tableBelongTo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldName.hashCode();
        result = 31 * result + tableBelongTo.hashCode();
        return result;
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
        jo.put("id", BIIDUtils.createFieldID(this));
        jo.put("table_id", getTableBelongTo().getID().getIdentityValue());
        return jo;
    }


    @Override
    public void writeXML(XMLPrintWriter writer) {

        writer.startTAG(XML_TAG);
        writer.attr("field_name", fieldName).attr("id", tableBelongTo.getID().getIdentityValue());
        writer.end();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.fieldName = reader.getAttrAsString("field_name", StringUtils.EMPTY);
            this.tableBelongTo = new BITable(new BITableID(reader.getAttrAsString("id", "__FINE_BI_EMPTY__")));
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIField{");
        sb.append("fieldName='").append(fieldName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}