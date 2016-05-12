package com.fr.bi.conf.data.source.operator.create;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by GUY on 2015/3/5.
 */
public class TableColumnRowTransOperator extends AbstractCreateTableETLOperator {


    public static final String XML_TAG = "TableColumnRowTransOperator";
    private static final long serialVersionUID = -9202056486680198263L;
    @BICoreField
    private String group_name;
    @BICoreField
    private String lc_name;
    @BICoreField
    private List<NameText> lc_value;
    @BICoreField
    private List<NameText> columns;

    private List<String> otherColumnNames;
    
    private transient DBTable basicTable;

    public TableColumnRowTransOperator(long userId) {
        super(userId);
    }

    public TableColumnRowTransOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }


    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("group_name", group_name);
        jo.put("lc_name", lc_name);
        JSONArray ja = new JSONArray();
        int size = lc_value.size();
        for (int i = 0; i < size; i++) {
            ja.put(new JSONArray(lc_value.get(i).createList()));
        }
        jo.put("lc_values", ja);

        ja = new JSONArray();
        size = columns.size();
        for (int i = 0; i < size; i++) {
            ja.put(new JSONArray(columns.get(i).createList()));
        }
        jo.put("columns", ja);
        return jo;
    }



    @Override
    public DBTable getBITable(DBTable[] tables) {
        if (basicTable == null) {
            initLCFieldNames(tables, user.getUserId());
        }
        return basicTable;
    }
    
    private void initBaseTable(List<? extends ITableSource> parents){
    	 if (basicTable == null) {
             DBTable[] base = new DBTable[parents.size()];
             for (int i = 0; i < base.length; i++) {
                 base[i] = parents.get(0).getDbTable();
             }
             initLCFieldNames(base, user.getUserId());
         }
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader) {
        initBaseTable(parents);
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents));
        return write(travel, ti);
    }

    private int write(Traversal<BIDataValue> travel, final ICubeTableService ti) {
        final int startIndex = otherColumnNames.size() + 1;
        final int lcCount = lc_value.size();
        final Map<Object, Integer> lcNameMap = getDistinctLcNameIndexMap(ti);
        ICubeColumnIndexReader groupMap = ti.loadGroup(new IndexKey(group_name), new ArrayList<BITableSourceRelation>());
        int row = 0;
        try {
            final int index = -1;
            Iterator<Entry<Object, GroupValueIndex>> iter = groupMap.iterator();
            while (iter.hasNext()) {
                Entry<Object, GroupValueIndex> entry = iter.next();
                final Object[] values = new Object[basicTable.getBIColumnLength()];
                values[0] = entry.getKey();
                GroupValueIndex gvi = entry.getValue();
                gvi.Traversal(new SingleRowTraversalAction() {
                    public boolean firstLineWrited = false;

                    @Override
                    public void actionPerformed(int rowIndices) {
                        if (!firstLineWrited) {
                            for (int i = 1; i < startIndex; i++) {
                                values[i] = ti.getRow(new IndexKey(otherColumnNames.get(i - 1)), rowIndices);
                            }
                            firstLineWrited = true;
                        }
                        Integer lcNameIndex = lcNameMap.get(ti.getRow(new IndexKey(lc_name), rowIndices));
                        if (lcNameIndex != null) {
                            for (int i = 0; i < columns.size(); i++) {
                                int cindex = lcNameIndex + i * lcCount + startIndex;
                                values[cindex] = ti.getRow(new IndexKey(columns.get(i).origin), rowIndices);
                            }
                        }
                    }
                });
                for (int k = 0; k < values.length; k++) {
                    travel.actionPerformed(new BIDataValue(row, k, values[k]));
                }
                row++;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return row;
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        initBaseTable(parents);
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents), start, end);
        return write(travel, ti);
    }


    private Map<Object, Integer> getDistinctLcNameIndexMap(ICubeTableService ti) {
        BIKey key = new IndexKey(lc_name);
        ICubeColumnIndexReader getter = ti.loadGroup(key);
        Map<Object, Integer> map = new HashMap<Object, Integer>();
        for (int i = 0; i < lc_value.size(); i++) {
            String str = lc_value.get(i).origin;
            Object ob = getter.createValue(str);
            map.put(ob, i);
        }
        return map;
    }

    private void initLCFieldNames(DBTable[] base, long userId) {

        this.basicTable = getBITable();

        for (DBTable pDBTable : base) {
            BIColumn column = pDBTable.getBIColumn(group_name);
            basicTable.addColumn(new BIColumn(group_name, group_name, column.getType(), false, column.getColumnSize(), column.getScale()));

            otherColumnNames = new ArrayList<String>();
            for (int i = 0; i < pDBTable.getBIColumnLength(); i++) {
                BIColumn tarColumn = pDBTable.getBIColumn(i);
                if (!isColumnSelected(tarColumn.getFieldName())) {
                    String fieldName = tarColumn.getFieldName();
                    otherColumnNames.add(fieldName);
                    basicTable.addColumn(new BIColumn(fieldName, null, tarColumn.getType(), false, tarColumn.getColumnSize(), tarColumn.getScale()));
                }
            }

            for (NameText column1 : columns) {
                BIColumn c = pDBTable.getBIColumn(column1.origin);
                for (NameText aLc_value : lc_value) {
                    String lcColumn = aLc_value.origin + "-" + column1.origin;
                    String text = aLc_value.getTransText() + "-" + column1.getTransText();
                    String lcColumnText = ComparatorUtils.equals(text, lcColumn) ? null : text;
                    basicTable.addColumn(new BIColumn(lcColumn, lcColumnText, c.getType(), false, c.getColumnSize(), c.getScale()));
                }
            }
        }

    }

    private boolean isColumnSelected(String name) {
        if (ComparatorUtils.equals(name, this.lc_name)) {
            return true;
        }
        if (ComparatorUtils.equals(name, this.group_name)) {
            return true;
        }
        for (NameText nt : this.columns) {
            if (ComparatorUtils.equals(nt.origin, name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("group_name")) {
            this.group_name = jo.getString("group_name");
        }

        if (jo.has("lc_name")) {
            this.lc_name = jo.getString("lc_name");
        }

        if (jo.has("columns")) {
            JSONArray ja = jo.getJSONArray("columns");
            columns = new ArrayList<NameText>();
            for (int i = 0; i < ja.length(); i++) {
                NameText nt = new NameText(ja.getJSONArray(i).getString(0), ja.getJSONArray(i).getString(1));
                if (!StringUtils.isEmpty(nt.origin)) {
                    columns.add(nt);
                }
            }
        }
        if (jo.has("lc_values")) {
            JSONArray ja = jo.getJSONArray("lc_values");
            lc_value = new ArrayList<NameText>();
            for (int i = 0; i < ja.length(); i++) {
                NameText nt = new NameText(ja.getJSONArray(i).getString(0), ja.getJSONArray(i).getString(1));
                if (!StringUtils.isEmpty(nt.origin)) {
                    lc_value.add(nt);
                }
            }
        }

    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isChildNode()) {
            String tag = reader.getTagName();
            if (ComparatorUtils.equals(tag, "group_lc")) {
                this.group_name = reader.getAttrAsString("group", "");
                this.lc_name = reader.getAttrAsString("lc", "");
//                this.md5 = reader.getAttrAsString("md5", StringUtils.EMPTY);
            } else if (ComparatorUtils.equals(tag, "lc_value")) {
                if (lc_value == null) {
                    lc_value = new ArrayList<NameText>();
                }
                lc_value.add(new NameText(reader.getAttrAsString("origin", ""), reader.getAttrAsString("text", "")));
            } else if (ComparatorUtils.equals(tag, "column")) {
                if (columns == null) {
                    columns = new ArrayList<NameText>();
                }
                columns.add(new NameText(reader.getAttrAsString("origin", ""), reader.getAttrAsString("text", "")));
            } else if (ComparatorUtils.equals(tag, "other_c_index")) {
                String s = reader.getAttrAsString("value", StringUtils.EMPTY);
                if (!StringUtils.isEmpty(s)) {
                    if (otherColumnNames == null) {
                        otherColumnNames = new ArrayList<String>();
                    }
                    otherColumnNames.add(s);
                }
            }
        }
    }


    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        writer.startTAG("group_lc");
        writer.attr("group", group_name).attr("lc", lc_name);
        writer.end();
        for (int i = 0; i < lc_value.size(); i++) {
            writer.startTAG("lc_value");
            NameText nt = lc_value.get(i);
            writer.attr("origin", nt.origin).attr("text", nt.originText);
            writer.end();
        }
        for (int i = 0; i < columns.size(); i++) {
            writer.startTAG("column");
            NameText nt = columns.get(i);
            writer.attr("origin", nt.origin).attr("text", nt.originText);
            writer.end();
        }
        if (otherColumnNames != null) {
            Iterator<String> it1 = otherColumnNames.iterator();
            while (it1.hasNext()) {
                writer.startTAG("other_c_index");
                writer.attr("value", it1.next());
                writer.end();
            }
        }
        writer.end();

    }

    private class NameText {
        private String origin;
        private String originText;

        private NameText(String origin, String originText) {
            this.origin = origin;
            this.originText = originText;
        }

        private String getTransText() {
            return StringUtils.isEmpty(originText) ? origin : originText;
        }

        private ArrayList<String> createList() {
            ArrayList<String> list = new ArrayList<String>();
            list.add(origin);
            list.add(originText);
            return list;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("NameText{");
            sb.append("origin='").append(origin).append('\'');
            sb.append(", originText='").append(originText).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

}