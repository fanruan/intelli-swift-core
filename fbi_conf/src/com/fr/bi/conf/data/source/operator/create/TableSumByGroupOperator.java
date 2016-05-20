package com.fr.bi.conf.data.source.operator.create;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.utils.TableIndexUtils;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by GUY on 2015/3/5.
 */
public class TableSumByGroupOperator extends AbstractCreateTableETLOperator {

    public static final String XML_TAG = "TableSumByGroupOperator";

    private static final long serialVersionUID = 388577142919399270L;
    private static final int SMALL_GROUP_LIMIT = 256;
    @BICoreField
    private SumByGroupTarget[] targets;
    @BICoreField
    private SumByGroupDimension[] dimensions;
    private JSONObject storedJson;

    public TableSumByGroupOperator(long userId) {
        super(userId);
    }

    public TableSumByGroupOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    /**
     * 锟斤拷Java锟斤拷锟斤拷转锟斤拷锟斤拷JSON锟斤拷锟斤拷
     *
     * @return JSON锟斤拷锟斤拷
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        return this.storedJson;
    }

    /**
     * 锟斤拷锟斤拷md5值
     *
     * @return MD5值
     */
//    @Override
//    public BICore fetchObjectCore() {
//        if (StringUtils.isEmpty(md5)) {
//            try {
//                MessageDigest digest = BIMD5Utils.getMessageDigest();
//                if (dimensions != null) {
//                    for (int i = 0; i < dimensions.length; i++) {
//                        digest.update(String.valueOf(dimensions[i].hashCode()).getBytes());
//                    }
//                }
//                if (targets != null) {
//                    for (int i = 0; i < targets.length; i++) {
//                        digest.update(String.valueOf(targets[i].hashCode()).getBytes());
//                    }
//                }
//                md5 = BIMD5Utils.getMD5String(digest.digest());
//            } catch (Exception e) {
//                md5 = StringUtils.EMPTY;
//                BILogger.getLogger().error(e.getMessage(), e);
//            }
//        }
//        return md5;
//    }

    @Override
    public IPersistentTable getBITable(IPersistentTable[] tables) {
        IPersistentTable persistentTable = getBITable();
        for (int k = 0; k < tables.length; k++) {
            IPersistentTable parent = tables[k];
            for (int i = 0; i < dimensions.length; i++) {
                if (parent.getField(dimensions[i].getName()).getBIType() == DBConstant.COLUMN.DATE) {
                    persistentTable.addColumn(new PersistentField(dimensions[i].getNameText(), dimensions[i].getGroup().getType() ==  BIReportConstant.GROUP.YMD ? Types.DATE : Types.INTEGER, 30));
                } else if (parent.getField(dimensions[i].getName()).getBIType() == DBConstant.COLUMN.NUMBER) {
                    persistentTable.addColumn(new PersistentField(dimensions[i].getNameText(), BIDBUtils.biTypeToSql(DBConstant.COLUMN.STRING), 30));
                } else {
                    persistentTable.addColumn(new PersistentField(dimensions[i].getNameText(), parent.getField(dimensions[i].getName()).getType(), parent.getField(dimensions[i].getName()).getColumnSize()));
                }
            }
            for (int i = 0; i < targets.length; i++) {
                persistentTable.addColumn(new PersistentField(targets[i].getNameText(),BIDBUtils.biTypeToSql(targets[i].getColumnType()), parent.getField(targets[i].getName()).getColumnSize()));
            }
        }
        return persistentTable;
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends ICubeTableSource> parents, ICubeDataLoader loader) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents));
        return write(travel, ti);
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends ICubeTableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        return  write(travel, loader.getTableIndex(getSingleParentMD5(parents), start, end));
    }

    private int write(Traversal<BIDataValue> travel, ICubeTableService ti) {
        if (dimensions == null || dimensions.length == 0) {
            return writeNoDimensionIndex(travel, ti);
        }
        GroupLine line = new GroupLine(ti, travel);
        ICubeColumnIndexReader[] getters = new ICubeColumnIndexReader[dimensions.length];
        for (int i = 0; i < getters.length; i++) {
            getters[i] = dimensions[i].getGroup().createGroupedMap(ti.loadGroup(dimensions[i].createKey(), new ArrayList<BITableSourceRelation>()));
        }
        Iterator<Entry<Object, GroupValueIndex>> iter = getters[0].iterator();
        while (iter.hasNext()) {
            Entry<Object, GroupValueIndex> entry = iter.next();
            line.fill(0, entry.getKey());
            writeIndexLineByLine(ti, entry.getValue(), getters, 1, line);
        }
        return line.index;
    }

    private int writeNoDimensionIndex(Traversal<BIDataValue> travel, ICubeTableService ti) {
        for (int i = 0; i < targets.length; i++) {
            travel.actionPerformed(new BIDataValue(0, i, targets[i].getSumValue(ti, ti.getAllShowIndex())));
        }
        return 1;
    }

    private void writeIndexLineByLine(ICubeTableService ti, GroupValueIndex pgvi, ICubeColumnIndexReader[] getters, int colIndex, GroupLine line) {
        if (pgvi == null || pgvi.getRowsCountWithData() == 0) {
            return;
        }
        if (colIndex == dimensions.length) {
            line.cal(pgvi);
            return;
        }
        if (isSmallNoneGroupDimension(ti, pgvi, colIndex)) {
            Object[] fieldValues = TableIndexUtils.getValueFromGvi(ti, new IndexKey(dimensions[colIndex].getName()), new GroupValueIndex[]{pgvi});
            String[] doubleValues = new String[fieldValues.length];
            IntList list = new IntList();
            for (int i = 0; i < fieldValues.length; i++) {
                if (fieldValues[i] == null) {
                    doubleValues[i] = StringUtils.EMPTY;
                    list.add(i);
                } else {
                    doubleValues[i] = fieldValues[i].toString();
                }
            }

            fieldValues = doubleValues;
            GroupValueIndex[] gvis = getters[colIndex].getGroupIndex(fieldValues);
            for (int i = 0; i < fieldValues.length; i++) {
                line.fill(colIndex, fieldValues[i]);
                GroupValueIndex cgvi = gvis[i];
                if (list.indexOf(i) != -1) {
                    cgvi = GVIFactory.createAllEmptyIndexGVI();
                }
                writeIndexLineByLine(ti, pgvi.AND(cgvi), getters, colIndex + 1, line);
            }
        } else {
            Iterator<Entry<Object, GroupValueIndex>> iter = getters[colIndex].iterator();
            while (iter.hasNext()) {
                Entry<Object, GroupValueIndex> entry = iter.next();
                line.fill(colIndex, entry.getKey());
                writeIndexLineByLine(ti, pgvi.AND(entry.getValue()), getters, colIndex + 1, line);
            }
        }
    }

    private boolean isSmallNoneGroupDimension(ICubeTableService ti, GroupValueIndex pgvi, int colIndex) {
        return pgvi.getRowsCountWithData() < SMALL_GROUP_LIMIT && (ti.getColumns().get(new IndexKey(dimensions[colIndex].getName())).getFieldType() == DBConstant.COLUMN.STRING) && dimensions[colIndex].getGroup().getType() != BIReportConstant.GROUP.CUSTOM_GROUP;
    }

    /**
     * dimensions:
     * targets:      sumery: distinct  锟街凤拷锟斤拷
     * 锟斤拷JSON锟斤拷锟斤拷转锟斤拷锟斤拷java锟斤拷锟斤拷
     *
     * @param jo json锟斤拷锟斤拷
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        List<SumByGroupDimension> dims = new ArrayList<SumByGroupDimension>();
        List<SumByGroupTarget> tars = new ArrayList<SumByGroupTarget>();
        this.storedJson = jo;
        if (jo.has("dimensions") && jo.has("view")) {
            JSONObject dimensionsAndTargets = jo.optJSONObject("dimensions");
            JSONObject view = jo.optJSONObject("view");
            JSONArray dimensionTypeArray = new JSONArray();
            JSONArray targetTypeArray = new JSONArray();
            if (view.has(BIReportConstant.REGION.DIMENSION1)) {
                dimensionTypeArray = view.getJSONArray(BIReportConstant.REGION.DIMENSION1);
            }
            if (view.has(BIReportConstant.REGION.TARGET1)) {
                targetTypeArray = view.getJSONArray(BIReportConstant.REGION.TARGET1);
            }
            for(int i = 0; i < dimensionTypeArray.length(); ++i){
                JSONObject dimension = dimensionsAndTargets.optJSONObject(dimensionTypeArray.getString(i));
                SumByGroupDimension dim = new SumByGroupDimension();
                dim.parseJSON(dimension);
                dims.add(dim);
            }
            for(int i = 0; i < targetTypeArray.length(); ++i){
                JSONObject tar = dimensionsAndTargets.optJSONObject(targetTypeArray.getString(i));
                SumByGroupTarget target = new SumByGroupTarget();
                target.parseJSON(tar);
                tars.add(target);
            }
        }
        this.dimensions = dims.toArray(new SumByGroupDimension[dims.size()]);
        this.targets = tars.toArray(new SumByGroupTarget[tars.size()]);
    }

    /**
     * 锟斤拷取锟接节点，应锟矫会被XMLableReader.readXMLObject()锟斤拷锟矫讹拷锟?
     *
     * @param reader XML锟斤拷取锟斤拷锟斤拷
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isChildNode()) {
            if (ComparatorUtils.equals(reader.getTagName(), "dim_targ")) {
                try {
                    JSONObject jo = new JSONObject(reader.getAttrAsString("values", StringUtils.EMPTY));
                    parseJSON(jo);
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * Write XML.<br>
     * The method will be invoked when save data to XML file.<br>
     * May override the method to save your own data.
     * 锟斤拷锟斤拷锟斤拷锟斤拷锟芥考锟角ｏ拷锟斤拷锟斤拷锟絯riter.print(), 锟斤拷锟斤拷锟斤拷writer.println()
     *
     * @param writer XML写锟斤拷锟斤拷锟?
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        writer.startTAG("dim_targ");
        try {
            writer.attr("values", this.storedJson.toString());
        } catch (Exception e) {
        }
        writer.end();
        writer.end();
    }

    private class GroupLine {
        private Object[] values;
        private ICubeTableService ti;
        private Traversal<BIDataValue> travel;
        private int index = 0;

        private GroupLine(ICubeTableService ti, Traversal<BIDataValue> travel) {
            this.ti = ti;
            this.travel = travel;
            values = new Object[dimensions.length + targets.length];
        }

        private void fill(int index, Object value) {
            values[index] = dimensions[index].getKeyValue(value);
        }

        private void cal(GroupValueIndex gvi) {
            for (int i = 0; i < targets.length; i++) {
                values[i + dimensions.length] = targets[i].getSumValue(ti, gvi);
            }
            write();
        }

        private void write() {
            for (int i = 0; i < values.length; i++) {
                travel.actionPerformed(new BIDataValue(index, i, values[i]));
            }
            index++;
        }
    }
}