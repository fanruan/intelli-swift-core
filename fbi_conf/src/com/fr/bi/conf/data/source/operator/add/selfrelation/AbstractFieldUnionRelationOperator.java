package com.fr.bi.conf.data.source.operator.add.selfrelation;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.data.source.operator.add.AbstractAddColumnOperator;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.security.MessageDigest;
import java.util.*;

/**
 * Created by GUY on 2015/3/5.
 */
public abstract class AbstractFieldUnionRelationOperator extends AbstractAddColumnOperator {
    private static final long serialVersionUID = -4723723974508198197L;
    @BICoreField
    protected LinkedHashMap<String, Integer> fields = new LinkedHashMap<String, Integer>();
    @BICoreField
    protected String idFieldName;
    @BICoreField
    protected List<String> showFields = new ArrayList<String>();

    AbstractFieldUnionRelationOperator(long userId) {
        super(userId);
    }

    AbstractFieldUnionRelationOperator() {
    }

    protected void readFields(XMLableReader reader) {
        if (ComparatorUtils.equals(reader.getTagName(), "floor")) {
            String floorName = reader.getAttrAsString("name", "");
            int length = reader.getAttrAsInt("length", 0);
            fields.put(floorName, length);
        }
        if (ComparatorUtils.equals(reader.getTagName(), "showfield")) {
            String field = reader.getAttrAsString("name", "");
            showFields.add(field);
        }
    }

    protected void writeFields(XMLPrintWriter writer) {
        Iterator<String> floorIterator = this.fields.keySet().iterator();
        while (floorIterator.hasNext()) {
            writer.startTAG("floor");
            String floorName = floorIterator.next();
            writer.attr("name", floorName);
            writer.end();
        }
        for (String s : showFields) {
            writer.startTAG("showfield");
            writer.attr("name", s);
            writer.end();
        }

    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("showfields")) {
            JSONArray ja = jo.getJSONArray("showfields");
            for (int i = 0; i < ja.length(); i++) {
                showFields.add(ja.getString(i));
            }
        }
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        jo.put("showfields", ja);
        for (String s : showFields) {
            ja.put(s);
        }
        return jo;
    }

    protected void digestFields(MessageDigest digest) {
        digest.update(idFieldName.getBytes());
        Iterator<Map.Entry<String, Integer>> iter = fields.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Integer> entry = iter.next();
            digest.update(entry.getKey().getBytes());
            digest.update(String.valueOf(entry.getValue()).getBytes());
        }
        for (String s : showFields) {
            digest.update(s.getBytes());
        }
    }

    @Override
    public IPersistentTable getBITable(IPersistentTable[] tables) {
        IPersistentTable persistentTable = getBITable();
        Iterator<Map.Entry<String, Integer>> it = fields.entrySet().iterator();
        for (IPersistentTable t : tables) {
            int type = DBConstant.CLASS.INTEGER;
            if (t.getField(idFieldName) != null) {
                type = t.getField(idFieldName).getBIType();
            }
            for (String s : showFields) {
                while (it.hasNext()) {
                    Map.Entry<String, Integer> entry = it.next();
                    persistentTable.addColumn(new UnionRelationPersistentField(s + "-" + entry.getKey(), BIDBUtils.biTypeToSql(type), entry.getValue()));
                }
            }
        }
        return persistentTable;
    }
}