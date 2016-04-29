package com.fr.bi.conf.data.source.operator.add;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Date;

/**
 * Created by GUY on 2015/3/5.
 */
public class SingleValueOperator extends AbstractAddColumnOperator {

    public static final String XML_TAG = "FieldFormulaOperator";
    private static final long serialVersionUID = -1675716963282566541L;

    @BICoreField
    private String value;

    public SingleValueOperator(long userId) {
        super(userId);
    }

    public SingleValueOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("value", value);
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
        if(jsonObject.has("value")){
        	value = jsonObject.getString("value");
        }
    }



    @Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        int rowCount = ti.getRowCount();
        Object value = checkValueType();
        for (long row = 0; row < rowCount; row++) {
            try {
                travel.actionPerformed(new BIDataValue(row, 0, value));
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
	private Object checkValueType() {
		Object v = value;
		switch (columnType){
			case DBConstant.COLUMN.NUMBER:{
				v = Double.parseDouble(value);
				break;
			}
			case DBConstant.COLUMN.DATE: {
				v = new Date(Long.valueOf(value).longValue());
				break;
			}
		}
		return v;
	}

	@Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isAttr()) {
            try {
                this.value = reader.getAttrAsString("value", StringUtils.EMPTY);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        try {
        	if(value != null){
        		writer.attr("value", value.toString());
        	}
        } catch (Exception e) {
        }
        writer.end();

    }
}