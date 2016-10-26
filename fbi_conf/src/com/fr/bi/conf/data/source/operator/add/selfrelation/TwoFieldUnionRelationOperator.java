package com.fr.bi.conf.data.source.operator.add.selfrelation;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.base.FRContext;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.sql.Types;
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
        Iterator<Map.Entry<String, Integer>> it;
        for (int i = 0; i < tables.length; i++) {
            IPersistentTable ptable = tables[i];
            int size = ptable.getField(idFieldName).getColumnSize();
//            int columnType = ptalbe.getField(idFieldName).getBIType();
            for (PersistentField field : ptable.getFieldList()) {
                persistentTable.addColumn(field);
            }
            for (String s : showFields) {
                it = fields.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Integer> entry = it.next();
//                persistentTable.addColumn(new UnionRelationPersistentField(entry.getKey(), BIDBUtils.biTypeToSql(columnType), size));
                    persistentTable.addColumn(new UnionRelationPersistentField(s + "-" + entry.getKey(), BIDBUtils.biTypeToSql(columnType), size, ptable.getField(idFieldName).getScale()));
                }
            }
        }
        return persistentTable;
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

    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, CubeTableSource source) {
        int rowCount = ti.getRowCount();
        int row = 0;
        ICubeColumnIndexReader idmap = ti.loadGroup(new IndexKey(idFieldName), new ArrayList<BITableSourceRelation>());
        ICubeFieldSource idColumn = ti.getColumns().get(new IndexKey(idFieldName));
        ICubeFieldSource pidColumn = ti.getColumns().get(new IndexKey(parentIdFieldName));
        int columnLength = fields.size();
        if (idColumn != null && pidColumn != null && idColumn.getFieldType() == pidColumn.getFieldType()) {
            ICubeColumnDetailGetter getter = ti.getColumnDetailReader(new IndexKey(idFieldName));
            Map<Object, Integer> valueIndexMap = new HashMap<Object, Integer>();
            Set<Object> isParent = new HashSet<Object>();
            Set<Integer> mustDelete = new HashSet<Integer>();
            for (int i = 0; i < rowCount; i++) {
                Object ob = getter.getValue(i);
                if (ob == null) {
                    continue;
                }
                valueIndexMap.put(ob, i);
            }

//            for (int i = 0; i < rowCount; i++) {
//                try {
//                    ArrayList<Number> list = new ArrayList<Number>();
//                    dealWithID(columnLength, i, list, idmap, ti, new IndexKey(idFieldName), new IndexKey(parentIdFieldName));
//                    for (int j = 1; j < list.size(); j++) {
//                        Object n = list.get(j);
//                        if (n != null) {
//                            int r = valueIndexMap.get(n);
//                            if (r >= 0) {
//                                isParent.add(n);
//                            }
//                        }
//                    }
//                } catch (StackOverflowError e) {
//                    FRContext.getLogger().error("dead circle at row:" + i, e);
//                }
//            }
//            for (Map.Entry<Object, Integer> entry : valueIndexMap.entrySet()) {
//                if (isParent.contains(entry.getKey())) {
//                    mustDelete.add(entry.getValue());
//                }
//            }
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
                Object[] res = idmap.createKey(fields.size() + columnLength * showFields.size());

                for (ICubeColumnDetailGetter gt : gts) {
                    res[index++] = gt.getValue(i);
                }


                ArrayList<Number> list = new ArrayList<Number>();
                Object[] tags = idmap.createKey(columnLength);
                dealWithID(columnLength, i, list, idmap, ti, new IndexKey(idFieldName), new IndexKey(parentIdFieldName));
                for (int k = list.size(), cnt = 0; k > 0 && cnt < list.size(); k--, cnt++) {
                    tags[cnt] = list.get(k - 1);
                }

                for (String s : showFields) {
                    ICubeColumnDetailGetter showGetter = ti.getColumnDetailReader(new IndexKey(s));
                    for (int j = 0; j < columnLength; j++) {
                        if (tags[j] != null) {
                            int r = valueIndexMap.get(tags[j]);
                            if (r >= 0) {
                                Object showOb = showGetter.getValue(r);
                                if (showOb != null) {
                                    res[index] = showOb;
                                }
                            }
                        }
                        index++;
                    }
                }
                for (int j = 0; j < index; j++) {
                    travel.actionPerformed(new BIDataValue(row, j, res[j]));
                }
                row++;
            }
        }
        return row;
    }

    private void dealWithID(final int cl, int i, final List list,
                            final ICubeColumnIndexReader idMap,
                            final ICubeTableService ti,
                            final BIKey idCIndex,
                            final BIKey pidCIndex) {
        Object id = ti.getColumnDetailReader(idCIndex).getValue(i);
        if (id != null && list.size() < cl) {
            list.add(id);
            Object pid = ti.getColumnDetailReader(pidCIndex).getValue(i);
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
