package com.fr.bi.conf.data.source.operator.add.date;


import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.AbstractAddColumnOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by GUY on 2015/3/5.
 */
public class GetValueFromDateOperator extends AbstractAddColumnOperator {
	
    public static final String XML_TAG = "DateDiffOperator";
    private static final long serialVersionUID = -1675716963282566541L;
    @BICoreField
    private String field;
    @BICoreField
    private String type;
    
    public GetValueFromDateOperator(long userId) {
        super(userId);
    }

    public GetValueFromDateOperator() {
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
        jo.put("item", item);
        jo.put("add_column_type", type);
        return jo;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("item")){
            JSONObject item = jo.getJSONObject("item");
            if (item.has("field")){
                field = item.getString("field");
            }
        }
        if(jo.has("add_column_type")){
        	type = jo.getString("add_column_type");
        }
    }


    @Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        int rowCount = ti.getRowCount();
        DateGetter dg = getDateGetter(type);
        BIKey key = new IndexKey(field);
        BIDateUtils.checkDateFieldType(ti.getColumns(), key);
        for (int row = 0; row < rowCount; row++) {
            long value = dg.get((Long)ti.getRow(key, row));
            try {
                travel.actionPerformed(new BIDataValue(row, startCol, value));
            } catch (Exception e) {
                BILogger.getLogger().error("incorrect formular");
                travel.actionPerformed(new BIDataValue(row, startCol, null));
            }
        }
        return rowCount;
    }


	@Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isAttr()) {
            try {
                this.field = reader.getAttrAsString("field", StringUtils.EMPTY);
                this.type = reader.getAttrAsString("type", BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_YEAR);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        try {
            writer.attr("field", field);
            writer.attr("type", type);
        } catch (Exception e) {
        }
        writer.end();

    }
    
	private static DateGetter getDateGetter(String type){
        if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_YEAR)) {
            return YearGetter.INSTANCE;
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_MONTH)) {
            return MonthGetter.INSTANCE;
        } else if (ComparatorUtils.equals(type, BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_SEASON)) {
            return SeasonGetter.INSTANCE;
        }
        return SeasonGetter.INSTANCE;
	}
	
	@Override
	protected int getSqlType(IPersistentTable[] tables){
		return java.sql.Types.INTEGER;
	}
}