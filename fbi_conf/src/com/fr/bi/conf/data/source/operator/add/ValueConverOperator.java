package com.fr.bi.conf.data.source.operator.add;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.GeneralUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Date;

/**
 * Created by 小灰灰 on 2016/5/9.
 */
public class ValueConverOperator extends AbstractAddColumnOperator {

    public static final String XML_TAG = "ValueConverOperator";
    private static final long serialVersionUID = -1675716963282566541L;
    @BICoreField
    private String field;
    public ValueConverOperator(long userId) {
        super(userId);
    }

    public ValueConverOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        JSONObject item = new JSONObject();
        item.put("field", field);
        item.put("field_type", columnType);
        jo.put("item", item);
        jo.put("add_column_type", BIJSONConstant.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT);
        return jo;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jsonObject json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        if (jsonObject.has("item")){
            JSONObject item = jsonObject.getJSONObject("item");
            if (item.has("field")){
                field = item.getString("field");
            }
        }
    }



    @Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        int rowCount = ti.getRowCount();
        BIKey key = new IndexKey(field);
        int fieldType = ti.getColumns().get(key).getFieldType();
        for (int row = 0; row < rowCount; row++) {
            Object value = checkValueType(ti.getRow(key, row), fieldType);
            try {
                travel.actionPerformed(new BIDataValue(row, startCol, value));
            } catch (Exception e) {
                BILogger.getLogger().error("incorrect formular");
                travel.actionPerformed(new BIDataValue(row, startCol, null));
            }
        }
        return rowCount;
    }

    /**
     * @return
     */
    private Object checkValueType(Object value, int fieldType) {
        if (value == null){
            return null;
        }
        switch (fieldType){
            case DBConstant.COLUMN.NUMBER:{
               return convertNumber((Number)value);
            }
            case DBConstant.COLUMN.DATE: {
                return convertDate((Long)value);
            }
            default:
                return convertString(value.toString());
        }
    }

    private Object convertString(String value) {
        switch (columnType){
            case DBConstant.COLUMN.NUMBER:{
                try {
                    return Double.parseDouble(value);
                } catch (Exception e) {
                }
                break;
            }
            case DBConstant.COLUMN.DATE: {
                try {
                    Date d = DateUtils.parse(value);
                    return d.getTime();
                } catch (Exception e) {
                }
                break;
            }
        }
        return null;
    }

    private Object convertDate(Long value) {
        switch (columnType){
            case DBConstant.COLUMN.NUMBER:{
                return value;
            }
            case DBConstant.COLUMN.STRING: {
                return DateUtils.format(new Date(value));
            }
        }
        return null;
    }

    private Object convertNumber(Number value) {
        switch (columnType){
            case DBConstant.COLUMN.STRING:{
                return GeneralUtils.objectToString(value);
            }
            case DBConstant.COLUMN.DATE: {
                return value.longValue();
            }
        }
        return null;
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isAttr()) {
            try {
                this.field = reader.getAttrAsString("field", StringUtils.EMPTY);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        try {
            if(field != null){
                writer.attr("field", field);
            }
        } catch (Exception e) {
        }
        writer.end();

    }
}
