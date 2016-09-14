package com.fr.bi.conf.data.source.operator.add.selfrelation;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

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

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader) {
        CubeTableSource source = getSingleParentMD5(parents);
        return write(travel, loader.getTableIndex(source), source);
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        CubeTableSource source = getSingleParentMD5(parents);
        return write(travel, loader.getTableIndex(source), source);
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

    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, CubeTableSource source) {
        int rowCount = ti.getRowCount();
        int columnLength = fields.size();
        int row = 0;
        ICubeFieldSource column = ti.getColumns().get(new IndexKey(idFieldName));
        if (column != null) {
            if (column.getFieldType() == DBConstant.COLUMN.STRING) {
                int[] groupLength = new int[columnLength];
                Iterator<Integer> it = fields.values().iterator();
                int k = 0;
                while (it.hasNext()) {
                    groupLength[k] = it.next();
                    k++;
                }
                ICubeColumnDetailGetter getter = ti.getColumnDetailReader(new IndexKey(idFieldName));
//                for (int i = 0; i < rowCount; i++) {
//                    Object ob = getter.getValue(i);
//                    if (ob == null){
//                        continue;
//                    }
//                    String v = ob.toString();
//                    v = dealWithLayerValue(v, groupLength);
//                    String[] res = new String[columnLength];
//                    if (v != null) {
//                        for (int j = 0; j < columnLength; j++) {
//                            if (v.length() >= groupLength[j]) {
//                                String result = v.substring(0, groupLength[j]);
//                                res[j] = dealWithValue(result);
//                            }
//                        }
//                    }
//                    for (int j = 0; j < columnLength; j++) {
//                        travel.actionPerformed(new BIDataValue(i, j + startCol, res[j]));
//                    }
//                }
                Map<String, Integer> valueIndexMap = new HashMap<String, Integer>();
                Set<String> isParent = new HashSet<String>();
                Set<Integer> mustDelete = new HashSet<Integer>();
                for (int i = 0; i < rowCount; i++) {
                    Object ob = getter.getValue(i);
                    if (ob == null) {
                        continue;
                    }
                    String v = ob.toString();
                    valueIndexMap.put(v, i);
                }
                for (int i = 0; i < rowCount; i++) {
                    Object ob = getter.getValue(i);
                    if (ob == null) {
                        continue;
                    }
                    String v = ob.toString();
                    v = dealWithLayerValue(v, groupLength);
                    if (v != null) {
                        for (int j = 0; j < columnLength; j++) {
                            if (v.length() >= groupLength[j]) {
                                String result = v.substring(0, groupLength[j]);
                                String layer = dealWithValue(result);
                                int r = valueIndexMap.get(layer);
                                if (r >= 0 && !ComparatorUtils.equals(v, result)) {
                                    isParent.add(layer);
                                }
                            }
                        }
                    }
                }

                for (Map.Entry<String, Integer> entry : valueIndexMap.entrySet()) {
                    if (isParent.contains(entry.getKey())) {
                        mustDelete.add(entry.getValue());
                    }
                }
                List<ICubeColumnDetailGetter> gts = new ArrayList<ICubeColumnDetailGetter>();
                List<PersistentField> fields = source.getPersistentTable().getFieldList();
                for (PersistentField field : fields) {
                    gts.add(ti.getColumnDetailReader(new IndexKey(field.getFieldName())));
                }
                for (int i = 0; i < rowCount; i++) {
                    if (mustDelete.contains(i)) {
                        continue;
                    }
                    int index = 0;
                    Object[] res = new Object[fields.size() + columnLength * showFields.size()];

                    for (ICubeColumnDetailGetter gt : gts) {
                        res[index++] = gt.getValue(i);
                    }

                    Object ob = getter.getValue(i);
                    if (ob == null) {
                        continue;
                    }
                    String v = ob.toString();
                    v = dealWithLayerValue(v, groupLength);

                    if (v != null) {
                        for (String s : showFields) {
                            ICubeColumnDetailGetter showGetter = ti.getColumnDetailReader(new IndexKey(s));
                            for (int j = 0; j < columnLength; j++) {
                                if (v.length() >= groupLength[j]) {
                                    String result = v.substring(0, groupLength[j]);
                                    String layer = dealWithValue(result);
                                    int r = valueIndexMap.get(layer);
                                    if (r >= 0) {
                                        Object showOb = showGetter.getValue(r);
                                        if (showOb != null) {
                                            res[index] = showOb.toString();
                                        }
                                    }
                                }
                                index++;
                            }
                        }
                    }
                    for (int j = 0; j < index; j++) {
                        travel.actionPerformed(new BIDataValue(row, j, res[j]));
                    }
                    row++;
                }
            }
        }
        return row;
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