package com.fr.bi.conf.data.source.operator.add.selfrelation;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.base.FRContext;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * Created by GUY on 2015/3/5.
 */
public class TwoFieldUnionRelationOperator extends AbstractFieldUnionRelationOperator {
    public static final String XML_TAG = "TwoFieldUnionRelationOprator";
    private static final long serialVersionUID = 3085155929448824378L;
    @BICoreField
    private String parentIdFieldName;

    public TwoFieldUnionRelationOperator(long userId) {
        super(userId);
    }


    public TwoFieldUnionRelationOperator() {
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
        jo.put("parentid_field_name", parentIdFieldName);
        JSONArray ja = new JSONArray();
        Iterator<String> iter = fields.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            JSONObject floor = new JSONObject();
            floor.put("name", name);
            ja.put(floor);
        }
        jo.put("floors", ja);
        return jo;
    }


    @Override
    public IPersistentTable getBITable(IPersistentTable[] tables) {
        IPersistentTable persistentTable = getBITable();
        Iterator<Map.Entry<String, Integer>> it = fields.entrySet().iterator();
        for (int i = 0; i < tables.length; i++) {
            IPersistentTable ptalbe = tables[i];
            int size = ptalbe.getField(idFieldName).getColumnSize();
            int columnType = ptalbe.getField(idFieldName).getBIType();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                persistentTable.addColumn(new UnionRelationPersistentField(entry.getKey(), BIDBUtils.biTypeToSql(columnType), size));
            }
        }
        return persistentTable;
    }

    @Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        int rowCount = ti.getRowCount();
        ICubeColumnIndexReader idmap = ti.loadGroup(new IndexKey(idFieldName), new ArrayList<BITableSourceRelation>());
        CubeFieldSource idColumn = ti.getColumns().get(new IndexKey(idFieldName));
        CubeFieldSource pidColumn = ti.getColumns().get(new IndexKey(parentIdFieldName));
        int columnLength = fields.size();
        if (idColumn != null && pidColumn != null && idColumn.getFieldType() == pidColumn.getFieldType()) {
            for (int i = 0; i < rowCount; i++) {
                try {
                    ArrayList<Number> list = new ArrayList<Number>();
                    Object[] res = idmap.createKey(columnLength);
                    dealWithID(columnLength, i, list, idmap, ti, new IndexKey(idFieldName), new IndexKey(parentIdFieldName));
                    for (int k = list.size(), index = 0; k > 0 && index < list.size(); k--, index++) {
                        res[index] = list.get(k - 1);
                    }
                    for (int j = 0; j < columnLength; j++) {
                        travel.actionPerformed(new BIDataValue(i, j + startCol, res[j] == null ? null : res[j]));
                    }
                } catch (StackOverflowError e) {
                    FRContext.getLogger().error("dead circle at row:" + i, e);
                }
            }
        }
        return rowCount;
    }

    private void dealWithID(final int cl, int i, final List list,
                            final ICubeColumnIndexReader idMap,
                            final ICubeTableService ti,
                            final BIKey idCIndex,
                            final BIKey pidCIndex) {
        Object id = ti.getRow(idCIndex, i);
        if (id != null && list.size() < cl) {
            list.add(id);
            Object pid = ti.getRow(pidCIndex, i);
            if (pid != null) {
                Object[] key = idMap.createKey(1);
                key[0] = pid;
                GroupValueIndex gvi = idMap.getGroupIndex(key)[0];
                if (gvi != null) {
                    gvi.BrokenableTraversal(new BrokenTraversalAction() {
                        @Override
                        public boolean actionPerformed(int rowIndex) {
                            dealWithID(cl, rowIndex, list, idMap, ti, idCIndex, pidCIndex);
                            return true;
                        }
                    });
                }
            }
        }
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
        parentIdFieldName = jsonObject.getString("parentid_field_name");
        JSONArray floor = jsonObject.getJSONArray("floors");
        fields = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < floor.length(); i++) {
            if (floor.getJSONObject(i).has("name")) {
                fields.put(floor.getJSONObject(i).getString("name"), 1);
            }
        }

    }

    @Override
    public void readXML(XMLableReader reader) {
        String tagName = reader.getTagName();
        super.readXML(reader);
        if (reader.isChildNode()) {
            readFields(reader);
        } else {
            if (ComparatorUtils.equals(tagName, XML_TAG)) {
                this.idFieldName = reader.getAttrAsString("id_field_name", "");
                this.parentIdFieldName = reader.getAttrAsString("parentid_field_name", "");
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        writer.attr("id_field_name", this.idFieldName)
                .attr("parentid_field_name", this.parentIdFieldName);

        writeFields(writer);
        writer.end();
    }
}