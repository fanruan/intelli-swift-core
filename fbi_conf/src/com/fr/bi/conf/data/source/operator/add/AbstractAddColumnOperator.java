package com.fr.bi.conf.data.source.operator.add;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.AbstractETLOperator;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.List;

/**
 * Created by GUY on 2015/3/5.
 */
public abstract class AbstractAddColumnOperator extends AbstractETLOperator {
    /**
	 * 
	 */
	private static final long serialVersionUID = 576233473241322504L;
    @BICoreField
	protected int columnType;
    @BICoreField
    protected String fieldName;

    protected AbstractAddColumnOperator(long userId) {
        super(userId);
    }

    protected AbstractAddColumnOperator() {
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("field_type", columnType);
        jo.put("field_name", fieldName);
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
        fieldName = jsonObject.getString("field_name");
        columnType = jsonObject.getInt("field_type");
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isAttr()) {
            fieldName = reader.getAttrAsString("field_name", "");
            this.columnType = reader.getAttrAsInt("column_type", 0);
//            md5 = reader.getAttrAsString("md5", StringUtils.EMPTY);
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        super.writeXML(writer);
        writer.attr("field_name", fieldName);
        writer.attr("column_type", columnType);
    }


    @Override
    public IPersistentTable getBITable(IPersistentTable[] tables) {
        IPersistentTable biTable = getBITable();
        biTable.addColumn(new PersistentField(fieldName, getSqlType()));
        return biTable;
    }
    
    protected int getSqlType(){
    	return BIDBUtils.biTypeToSql(columnType);
    }

    @Override
	public boolean isAddColumnOprator() {
        return true;
    }

    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader){
        return write(travel, loader.getTableIndex(getSingleParentMD5(parents)), 0);
    }

    protected abstract int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol);

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        return write(travel, loader.getTableIndex(getSingleParentMD5(parents), start, end), startCol);
    }
}