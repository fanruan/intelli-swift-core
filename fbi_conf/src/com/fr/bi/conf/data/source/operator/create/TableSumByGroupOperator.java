package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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
            for (int i = 0; i < getDimensions().length; i++) {
                if (parent.getField(getDimensions()[i].getName()).getBIType() == DBConstant.COLUMN.DATE) {
                    persistentTable.addColumn(new PersistentField(getDimensions()[i].getNameText(), getDimensions()[i].getGroup().getType() == BIReportConstant.GROUP.YMD ? Types.DATE : Types.INTEGER, 30));
                } else if (parent.getField(dimensions[i].getName()).getBIType() == DBConstant.COLUMN.NUMBER) {
                    PersistentField pfield = parent.getField(getDimensions()[i].getName());
                    /**
                     * 数值类型自定义分组，类型要为String类型
                     */
                    persistentTable.addColumn(generateSumNumberGroup(getDimensions()[i], pfield));
                } else {
                    persistentTable.addColumn(new PersistentField(getDimensions()[i].getNameText(), parent.getField(dimensions[i].getName()).getSqlType(), parent.getField(dimensions[i].getName()).getColumnSize()));
                }
            }
            for (int i = 0; i < getTargets().length; i++) {
                persistentTable.addColumn(new PersistentField(getTargets()[i].getNameText(), BIDBUtils.biTypeToSql(targets[i].getColumnType()), parent.getField(targets[i].getName()).getColumnSize()));
            }
        }
        return persistentTable;
    }

    private PersistentField generateSumNumberGroup(SumByGroupDimension sum, PersistentField parentField) {
        int type = BIDBUtils.biTypeToSql(DBConstant.COLUMN.STRING);
        if (sum.getGroup().getType() == BIReportConstant.GROUP.ID_GROUP || sum.getGroup().getType() == BIReportConstant.GROUP.NO_GROUP) {
            type = parentField.getSqlType();
        }
        return new PersistentField(sum.getNameText(), sum.getNameText(), type, parentField.getColumnSize(), parentField.getScale());
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents));
        return write(travel, ti);
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        if (start == 0) {
            end = Integer.MAX_VALUE;
            return write(travel, loader.getTableIndex(getSingleParentMD5(parents), start, end));
        } else {
            return 0;
        }
    }

    private int write(Traversal<BIDataValue> travel, ICubeTableService ti) {
        if (getDimensions().length == 0) {
            return writeNoDimensionIndex(travel, ti);
        }
        BIKey[] keys = new BIKey[getDimensions().length];
        IGroup[] groups = new IGroup[getDimensions().length];
        for (int i = 0; i < groups.length; i++){
            keys[i] = getDimensions()[i].createKey();
            groups[i] = getDimensions()[i].getGroup();
        }

        ValueIterator iterator = new ValueIterator(ti, keys, groups);
        int index = 0;
        while (iterator.hasNext()) {
            ValuesAndGVI valuesAndGVI = iterator.next();
            write(ti, valuesAndGVI, index, travel);
            index++;
        }
        return index;
    }

    private void write(ICubeTableService ti, ValuesAndGVI valuesAndGVI, int index, Traversal<BIDataValue> travel) {
        int col = 0;
        for (int i = 0; i < valuesAndGVI.values.length; i++){
            travel.actionPerformed(new BIDataValue(index, col, getDimensions()[i].getKeyValue(valuesAndGVI.values[i])));
            col++;
        }
        for (int i = 0; i < getTargets().length; i++) {
            travel.actionPerformed(new BIDataValue(index, col, getTargets()[i].getSumValue(ti, valuesAndGVI.gvi)));
            col++;
        }
    }

    private int writeNoDimensionIndex(Traversal<BIDataValue> travel, ICubeTableService ti) {
        for (int i = 0; i < getTargets().length; i++) {
            travel.actionPerformed(new BIDataValue(0, i, getTargets()[i].getSumValue(ti, ti.getAllShowIndex())));
        }
        return 1;
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
            for (int i = 0; i < dimensionTypeArray.length(); ++i) {
                JSONObject dimension = dimensionsAndTargets.optJSONObject(dimensionTypeArray.getString(i));
                SumByGroupDimension dim = new SumByGroupDimension();
                dim.parseJSON(dimension);
                dims.add(dim);
            }
            for (int i = 0; i < targetTypeArray.length(); ++i) {
                JSONObject tar = dimensionsAndTargets.optJSONObject(targetTypeArray.getString(i));
                SumByGroupTarget target = new SumByGroupTarget();
                target.parseJSON(tar);
                tars.add(target);
            }
        }
        this.dimensions = dims.toArray(new SumByGroupDimension[dims.size()]);
        this.targets = tars.toArray(new SumByGroupTarget[tars.size()]);
    }

    private SumByGroupTarget[] getTargets() {
        if (this.targets == null) {
            return new SumByGroupTarget[0];
        }
        return this.targets;
    }

    private SumByGroupDimension[] getDimensions() {
        if (this.dimensions == null) {
            return new SumByGroupDimension[0];
        }
        return this.dimensions;
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
                    init(reader.getAttrAsString("values", StringUtils.EMPTY));
                } catch (Exception e) {

                }
            }
        }
    }

    private void init(String values) throws Exception {
        parseJSON(new JSONObject(values));
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
}