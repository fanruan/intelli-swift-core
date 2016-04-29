package com.fr.bi.conf.data.source.operator.add.selfrelation;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.stable.data.db.BIDataValue;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by GUY on 2015/3/5.
 */
public class OneFieldUnionRelationOperator extends AbstractFieldUnionRelationOperator {

    public static final String XML_TAG = "OneFieldUnionRelationOprator";
    private static final long serialVersionUID = -5799989770538497335L;

    public OneFieldUnionRelationOperator(long userId) {
        super(userId);
    }


    public OneFieldUnionRelationOperator() {
        // TODO Auto-generated constructor stub
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
        JSONObject jo = super.createJSON();
        jo.put("id_field_name", idFieldName);
        JSONArray ja = new JSONArray();
        Iterator<Map.Entry<String, Integer>> iter = fields.entrySet().iterator();
        while (iter.hasNext()) {
            JSONObject floor = new JSONObject();
            Map.Entry<String, Integer> entry = iter.next();
            floor.put("name", entry.getKey());
            floor.put("length", entry.getValue());
            ja.put(floor);
        }
        jo.put("floors", ja);
        return jo;
    }


    @Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        int rowCount = ti.getRowCount();
        int columnLength = fields.size();
        BIBasicField column = ti.getColumns().get(new IndexKey(idFieldName));
        if (column != null) {
            if (column.getFieldType() == DBConstant.COLUMN.STRING) {
                int[] groupLength = new int[columnLength];
                Iterator<Integer> it = fields.values().iterator();
                int k = 0;
                while (it.hasNext()) {
                    groupLength[k] = it.next();
                    k++;
                }

                for (long i = 0; i < rowCount; i++) {
                    String v = ti.getRow(new IndexKey(idFieldName), (int) i).toString();
                    v = dealWithLayerValue(v, groupLength);
                    String[] res = new String[columnLength];
                    if (v != null) {
                        for (int j = 0; j < columnLength; j++) {
                            if (v.length() >= groupLength[j]) {
                                String result = v.substring(0, groupLength[j]);
                                res[j] = dealWithValue(result);
                            }
                        }
                    }
                    for (int j = 0; j < columnLength; j++) {
                        travel.actionPerformed(new BIDataValue(i, j + startCol, res[j]));
                    }
                }

            }
        }
        return rowCount;
    }

    public String dealWithLayerValue(String v, int[] cz) {
        return v;
    }

    public String dealWithValue(String value) {
        return value;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jsonObject json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        idFieldName = jsonObject.getString("id_field_name");
        JSONArray floor = jsonObject.getJSONArray("floors");
        fields = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < floor.length(); i++) {
            JSONObject floorJo = floor.getJSONObject(i);
            fields.put(floorJo.getString("name"), floorJo.getInt("length"));
        }
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        String tagName = reader.getTagName();
        if (reader.isChildNode()) {
            readFields(reader);
        } else {
            if (ComparatorUtils.equals(tagName, XML_TAG)) {
                this.idFieldName = reader.getAttrAsString("id_field_name", "");
            }
        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        writer.attr("id_field_name", this.idFieldName);
        writeFields(writer);
        writer.end();
    }
}