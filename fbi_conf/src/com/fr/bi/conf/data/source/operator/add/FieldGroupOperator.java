package com.fr.bi.conf.data.source.operator.add;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.PersistentTable;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.operation.group.data.string.StringGroupInfo;
import com.fr.bi.stable.operation.group.group.CustomGroup;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by GUY on 2015/3/5.
 */
public class FieldGroupOperator extends AbstractAddColumnOperator {

    public static final String XML_TAG = "BIFieldGroupOprator";
    private static final long serialVersionUID = 2357582136227706580L;
    @BICoreField
    private String targetFieldName;
    @BICoreField
    private CustomGroup group;

    public FieldGroupOperator(long userId) {
        super(userId);
    }


    public FieldGroupOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return JSON对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject tableInfor = new JSONObject();
        tableInfor.put("target_field_name", targetFieldName);
        tableInfor.put("field_name", fieldName);
        jo.put("table_infor", tableInfor);
        jo.put("group", group.createJSON());
        return jo;
    }



    @Override
    public PersistentTable getBITable(PersistentTable[] tables) {
        PersistentTable persistentTable = getBITable();
        for (PersistentTable table : tables) {
            persistentTable.addColumn(new BIColumn(fieldName, BIDBUtils.biTypeToSql(DBConstant.COLUMN.STRING), table.getBIColumn(targetFieldName).getColumnSize()));
        }
        return persistentTable;
    }

    @Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        Map resMap = new HashMap();
        for (StringGroupInfo info : group.getGroups()) {
            Set values = info.createValueSet();
            for (Object aValue : values) {
                resMap.put(aValue, info.getValue());
            }
        }

        int rowCount = ti.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            Object v = ti.getRowValue(new IndexKey(targetFieldName),  row);
            Object value = resMap.get(v);
            if (value == null ){
                value =  group.ungroup2Other() ? group.getUngroup2OtherName() : v;
            }
            travel.actionPerformed(new BIDataValue(row, startCol, value));
        }
        return rowCount;

    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jsonObject json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        JSONObject groupTable = jsonObject.getJSONObject("table_infor");
        group = new CustomGroup();
        group.parseJSON(jsonObject.getJSONObject("group"));
        fieldName = groupTable.getString("field_name");
        targetFieldName = groupTable.getString("target_field_name");
    }


    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isChildNode()) {
            group = new CustomGroup();
            reader.readXMLObject(group);
        } else {
            this.targetFieldName = reader.getAttrAsString("target_field_name", StringUtils.EMPTY);
            this.fieldName = reader.getAttrAsString("field_name", StringUtils.EMPTY);

        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        writer.attr("target_field_name", this.targetFieldName);
        group.writeXML(writer);
        writer.end();

    }

}