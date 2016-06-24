package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by GUY on 2015/3/5.
 */
public class TableJoinOperator extends AbstractCreateTableETLOperator {

    public static final String XML_TAG = "TableJoinOperator";

    private static final long serialVersionUID = -5395803667343259448L;

    @BICoreField
    private int type;

    @BICoreField
    private List<JoinColumn> columns = new ArrayList<JoinColumn>();
    @BICoreField
    private List<String> left = new ArrayList<String>();
    @BICoreField
    private List<String> right = new ArrayList<String>();

    public TableJoinOperator(long userId) {
        super(userId);
    }

    public TableJoinOperator() {
    }

    public TableJoinOperator(int type, List<JoinColumn> columns, List<String> left, List<String> right) {
        this.type = type;
        this.columns = columns;
        this.left = left;
        this.right = right;
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
        jo.put("join_style", type);
        JSONArray lr = new JSONArray();
        for (int i = 0; i < left.size(); i++) {
            JSONArray value = new JSONArray();
            value.put(left.get(i));
            value.put(right.get(i));
            lr.put(value);
        }
        jo.put("join_fields", lr);
        JSONArray fields = new JSONArray();
        for (int i = 0; i < columns.size(); i++) {
            fields.put(columns.get(i).createJSON());
        }
        jo.put("join_names", fields);
        return jo;
    }


    @Override
    public IPersistentTable getBITable(IPersistentTable[] tables) {
        IPersistentTable persistentTable = getBITable();
        IPersistentTable leftT = tables[0];
        IPersistentTable rightT = tables[1];
        for (int i = 0; i < columns.size(); i++) {
            PersistentField column = columns.get(i).isLeft() ? leftT.getField(columns.get(i).getColumnName()) : rightT.getField(columns.get(i).getColumnName());
            if (column != null) {
                persistentTable.addColumn(new PersistentField(columns.get(i).getName(), columns.get(i).getName(), column.getSqlType(), column.isPrimaryKey(), column.getColumnSize(), column.getScale()));
            }
        }
        return persistentTable;
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader) {
        if (parents == null || parents.size() != 2) {
            throw new RuntimeException("invalid join parents");
        }
        ICubeTableService lti = loader.getTableIndex(parents.get(0));
        ICubeTableService rti = loader.getTableIndex(parents.get(1));
        return write(travel, lti, rti);
    }

    private int write(Traversal<BIDataValue> travel, ICubeTableService lti, ICubeTableService rti) {
        if (type == BIBaseConstant.JOINTYPE.OUTER) {
            return writeIndex(travel, lti, rti, false, true);
        } else if (type == BIBaseConstant.JOINTYPE.INNER) {
            return writeIndex(travel, lti, rti, true, false);
        } else if (type == BIBaseConstant.JOINTYPE.LEFT) {
            return writeIndex(travel, lti, rti, false, false);
        } else {
            return writeRIndex(travel, lti, rti);
        }
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        if (parents == null || parents.size() != 2) {
            throw new RuntimeException("invalid join parents");
        }
        ICubeTableService lti = loader.getTableIndex(parents.get(0), start, end);
        ICubeTableService rti = loader.getTableIndex(parents.get(1), start, end);
        return write(travel, lti, rti);
    }


    private int writeRIndex(Traversal<BIDataValue> travel, ICubeTableService lti, ICubeTableService rti) {
        int rlen = getColumnSize(false);
        ArrayList<ICubeColumnIndexReader> getter = new ArrayList<ICubeColumnIndexReader>();
        for (int i = 0; i < left.size(); i++) {
            getter.add(lti.loadGroup(new IndexKey(left.get(i)), new ArrayList<BITableSourceRelation>()));
        }
        int index = 0;
        int lleftCount = getColumnSize(true);
        GroupValueIndex rTotalGvi = null;
        long row = rti.getRowCount();
        for (int i = 0; i < row; i++) {
            GroupValueIndex gvi = null;
            Object[] rvalues = new Object[rlen];
            for (int j = 0; j < rlen; j++) {
                rvalues[j] = rti.getColumnDetailReader(new IndexKey(columns.get(j < right.size() ? j : lleftCount + j).getColumnName())).getValue(i);
            }
            for (int j = 0; j < right.size(); j++) {
                Object[] key = getter.get(j).createKey(1);
                key[0] = rvalues[j] instanceof Date ? ((Date) rvalues[j]).getTime() : rvalues[j];
                GroupValueIndex rgvi = key[0] == null ? null : getter.get(j).getGroupIndex(key)[0];
                if (rgvi == null) {
                    gvi = null;
                    break;
                }
                if (gvi == null) {
                    gvi = rgvi;
                } else {
                    gvi = gvi.AND(rgvi);
                }
            }
            if (rTotalGvi == null) {
                rTotalGvi = gvi;
            } else {
                rTotalGvi = rTotalGvi.OR(gvi);
            }
            index = rtravel(travel, lti, rlen, index, gvi, rvalues, lleftCount);
        }
        return index;
    }


    private int rtravel(Traversal<BIDataValue> travel, ICubeTableService lti, int rlen, int index, GroupValueIndex gvi, Object[] rvalues, int lleftCount) {
        if (gvi == null || gvi.getRowsCountWithData() == 0) {
            for (int j = 0; j < rlen; j++) {
                travel.actionPerformed(new BIDataValue(index, j < right.size() ? j : lleftCount + j, rvalues[j]));
            }
            for (int j = 0; j < lleftCount; j++) {
                travel.actionPerformed(new BIDataValue(index, right.size() + j, null));
            }
            index++;
        } else {
            final IntList rRows = new IntList();
            gvi.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int rowIndices) {
                    rRows.add(rowIndices);
                }
            });
            for (int k = 0; k < rRows.size(); k++) {
                for (int j = 0; j < rlen; j++) {
                    travel.actionPerformed(new BIDataValue(index, j < right.size() ? j : lleftCount + j, rvalues[j]));
                }
                for (int j = right.size(); j < lti.getColumns().size(); j++) {
                    travel.actionPerformed(new BIDataValue(index, j, lti.getColumnDetailReader(new IndexKey(columns.get(j).getColumnName())).getValue(rRows.get(k))));
                }
                index++;
            }
        }
        return index;
    }


    private int writeIndex(Traversal<BIDataValue> travel, ICubeTableService lti, ICubeTableService rti, boolean nullContinue, boolean writeLeft) {
        int llen = getColumnSize(true);
        ArrayList<ICubeColumnIndexReader> getter = new ArrayList<ICubeColumnIndexReader>();
        for (int i = 0; i < right.size(); i++) {
            getter.add(rti.loadGroup(new IndexKey(right.get(i)), new ArrayList<BITableSourceRelation>()));
        }
        int index = 0;
        GroupValueIndex rTotalGvi = null;
        long row = lti.getRowCount();
        for (int i = 0; i < row; i++) {
            GroupValueIndex gvi = null;
            Object[] lvalues = new Object[llen];
            for (int j = 0; j < llen; j++) {
                lvalues[j] = lti.getColumnDetailReader(new IndexKey(columns.get(j).getColumnName())).getValue(i);
            }
            for (int j = 0; j < left.size(); j++) {
                Object[] key = getter.get(j).createKey(1);
                key[0] = lvalues[j] instanceof Date ? ((Date) lvalues[j]).getTime() : lvalues[j];
                ICubeColumnIndexReader reader = getter.get(j);
                GroupValueIndex rgvi = key[0] == null ? null : reader.getGroupIndex(key)[0];
                if (rgvi == null) {
                    gvi = null;
                    break;
                }
                if (gvi == null) {
                    gvi = rgvi;
                } else {
                    gvi = gvi.AND(rgvi);
                }
            }
            if (rTotalGvi == null) {
                rTotalGvi = gvi;
            } else {
                rTotalGvi = rTotalGvi.OR(gvi);
            }
            index = travel(travel, rti, llen, index, gvi, lvalues, nullContinue);
        }
        return writeLeft ? writeLeftIndex(rTotalGvi, rti, llen, index, travel) : index;
    }

    private int travel(Traversal<BIDataValue> travel, ICubeTableService rti, int llen, int index, GroupValueIndex gvi, Object[] lvalues, boolean nullContinue) {
        if (gvi == null || gvi.getRowsCountWithData() == 0) {
            if (nullContinue) {
                return index;
            }
            for (int j = 0; j < llen; j++) {
                travel.actionPerformed(new BIDataValue(index, j, lvalues[j]));
            }
            for (int j = llen; j < columns.size(); j++) {
                travel.actionPerformed(new BIDataValue(index, j, null));
            }
            index++;
        } else {
            final IntList rRows = new IntList();
            gvi.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int rowIndices) {
                    rRows.add(rowIndices);
                }
            });
            for (int k = 0; k < rRows.size(); k++) {
                for (int j = 0; j < llen; j++) {
                    travel.actionPerformed(new BIDataValue(index, j, lvalues[j]));
                }
                for (int j = llen; j < columns.size(); j++) {
                    travel.actionPerformed(new BIDataValue(index, j, rti.getColumnDetailReader(new IndexKey(columns.get(j).getColumnName())).getValue(rRows.get(k))));
                }
                index++;
            }
        }
        return index;
    }

    private int writeLeftIndex(GroupValueIndex rTotalGvi, ICubeTableService rti, int llen, int index, Traversal<BIDataValue> travel) {
        GroupValueIndex rLeft = rTotalGvi == null ? rti.getAllShowIndex() : rTotalGvi.NOT(rti.getRowCount()).AND(rti.getAllShowIndex());
        final IntList rLeftRows = new IntList();
        rLeft.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int rowIndices) {
                rLeftRows.add(rowIndices);
            }
        });
        for (int k = 0; k < rLeftRows.size(); k++) {
            for (int j = 0; j < llen; j++) {
                travel.actionPerformed(new BIDataValue(index, j, null));
            }
            for (int j = llen; j < columns.size(); j++) {
                travel.actionPerformed(new BIDataValue(index, j, rti.getColumnDetailReader(new IndexKey(columns.get(j).getColumnName())).getValue(rLeftRows.get(k))));
            }
            index++;
        }
        return index;
    }


    /**
     * join_style: [], join类型
     * join_fields: [[ firstFieldName, secondFieldName]],
     * join_names: [  ],
     * table_name: 表名
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        type = jo.getInt("join_style");
        JSONArray lr = jo.getJSONArray("join_fields");
        for (int i = 0; i < lr.length(); i++) {
            JSONArray value = lr.getJSONArray(i);
            left.add(value.getString(0));
            right.add(value.getString(1));
        }
        JSONArray fields = jo.getJSONArray("join_names");
        for (int i = 0; i < fields.length(); i++) {
            JoinColumn column = new JoinColumn();
            column.parseJSON(fields.getJSONObject(i));
            columns.add(column);
        }
    }

    private int getLeftIndex(String name) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isLeft() && ComparatorUtils.equals(columns.get(i).getColumnName(), name)) {
                return i;
            }
        }
        String message = "can`t find column : " + name;
        BILogger.getLogger().info(message);
        throw new RuntimeException(message);
    }

    private int getRightIndex(String name) {
        for (int i = 0; i < columns.size(); i++) {
            if (!columns.get(i).isLeft() && ComparatorUtils.equals(columns.get(i).getColumnName(), name)) {
                return i;
            }
        }
        String message = "can`t find column : " + name;
        BILogger.getLogger().info(message);
        throw new RuntimeException(message);
    }

    /**
     * 读取子节点，应该会被XMLableReader.readXMLObject()调用多次
     *
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isChildNode()) {
            String tag = reader.getTagName();
            if ("left".equals(tag)) {
                left.add(reader.getAttrAsString("value", StringUtils.EMPTY));
            } else if ("right".equals(tag)) {
                right.add(reader.getAttrAsString("value", StringUtils.EMPTY));
            } else if (JoinColumn.XML_TAG.equals(tag)) {
                JoinColumn column = new JoinColumn();
                column.readXML(reader);
                columns.add(column);
            }
        }
        if (reader.isAttr()) {
            type = reader.getAttrAsInt("type", 1);
        }
    }

    /**
     * Write XML.<br>
     * The method will be invoked when save data to XML file.<br>
     * May override the method to save your own data.
     * 从性能上面考虑，大家用writer.print(), 而不是writer.println()
     *
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        writer.attr("type", type);
        for (int i = 0; i < left.size(); i++) {
            writer.startTAG("left");
            writer.attr("value", left.get(i));
            writer.end();
        }
        for (int i = 0; i < right.size(); i++) {
            writer.startTAG("right");
            writer.attr("value", right.get(i));
            writer.end();
        }
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).writeXML(writer);
        }
        writer.end();

    }

    public int getColumnSize(boolean isLeft) {
        int i = 0;
        for (JoinColumn c : columns) {
            if (c.isLeft() == isLeft) {
                i++;
            }
        }
        return i;
    }
}