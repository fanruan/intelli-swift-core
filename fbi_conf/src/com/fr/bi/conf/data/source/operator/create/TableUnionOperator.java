package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GUY on 2015/3/5.
 */
public class TableUnionOperator extends AbstractCreateTableETLOperator {
    public static final String XML_TAG = "TableUnionOperator";
    private static final long serialVersionUID = -7588810240876090654L;
    @BICoreField
    private List<List<String>> lists = new ArrayList<List<String>>();

    public TableUnionOperator(long userId) {
        super(userId);
    }

    public TableUnionOperator() {
    }

    public TableUnionOperator(List<List<String>> lists) {
        this.lists = lists;
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
        JSONArray ja = new JSONArray();
        for (int i = 0; i < lists.size(); i++) {
            List<String> al = lists.get(i);
            JSONArray value = new JSONArray();
            for (int j = 0; j < al.size(); j++) {
                value.put(al.get(j));
            }
            ja.put(value);
        }
        jo.put("union_array", ja);
        return jo;
    }

//    /**
//     * 创建md5值
//     *
//     * @return MD5值
//     */
//    @Override
//    public BICore fetchObjectCore() {
//        if (StringUtils.isEmpty(md5)) {
//            try {
//                MessageDigest digest = BIMD5Utils.getMessageDigest();
//                digest.update(lists.toString().getBytes());
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
        IPersistentTable[] pts = new IPersistentTable[tables.length];
        for (int i = 0; i < pts.length; i++) {
            pts[i] = tables[i];
        }

        for (int i = 0; i < lists.size(); i++) {
            List<String> list = lists.get(i);
            int bitype = 0;
            int columnSize = 0;
            for (int j = 1; j < list.size(); j++) {
                if (!StringUtils.isEmpty(list.get(j))) {
                    PersistentField column = pts[j - 1].getField(list.get(j));
                    bitype = column.getBIType();
                    columnSize = Math.max(columnSize, column.getColumnSize());
                }
            }
            persistentTable.addColumn(new PersistentField(list.get(0), BIDBUtils.biTypeToSql(bitype), columnSize));
        }

        return persistentTable;
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader) {
        List<ICubeTableService> tis = new ArrayList<ICubeTableService>();
        for (CubeTableSource s : parents) {
            tis.add(loader.getTableIndex(s));
        }
        return write(travel, tis);
    }

    private int write(Traversal<BIDataValue> travel, List<ICubeTableService> tis) {
        int index = 0;
        for (int i = 0; i < tis.size(); i++) {
            ICubeTableService ti = tis.get(i);
            CubeFieldSource[] cIndex = new CubeFieldSource[lists.size()];
            for (int j = 0; j < cIndex.length; j++) {
                cIndex[j] = ti.getColumns().get(new IndexKey(lists.get(j).get(i + 1)));
            }
            long row = ti.getRowCount();
            for (int j = 0; j < row; j++) {
                for (int k = 0; k < lists.size(); k++) {
                    if (cIndex[k] == null) {
                        travel.actionPerformed(new BIDataValue(index, k, null));
                    } else {
                        Object ob = ti.getRow(new IndexKey(cIndex[k].getFieldName()), j);
                        travel.actionPerformed(new BIDataValue(index, k, (cIndex[k].getFieldType() == DBConstant.COLUMN.NUMBER && ob != null) ? ((Number) ob).doubleValue() : ob));
                    }
                }
                index++;
            }
        }
        return index;
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        int st = (int) Math.ceil(start / parents.size());
        int ed = (int) Math.ceil(end / parents.size());
        List<ICubeTableService> tis = new ArrayList<ICubeTableService>();
        for (CubeTableSource s : parents) {
            tis.add(loader.getTableIndex(s, st, ed));
        }
        return write(travel, tis);
    }


    /**
     * table_name: ,                                        表名
     * union_array: ,    a * (n+1) 维数组
     * union_names: []
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        JSONArray ja = jo.getJSONArray("union_array");
        for (int i = 0; i < ja.length(); i++) {
            JSONArray value = ja.getJSONArray(i);
            ArrayList<String> al = new ArrayList<String>();
            for (int j = 0; j < value.length(); j++) {
                al.add(value.getString(j));
            }
            lists.add(al);
        }
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
            if ("row".equals(reader.getTagName())) {
                final ArrayList<String> list = new ArrayList<String>();
                lists.add(list);
                reader.readXMLObject(new XMLReadable() {
                    @Override
                    public void readXML(XMLableReader reader) {
                        if (reader.isChildNode()) {
                            list.add(reader.getAttrAsString("value", StringUtils.EMPTY));
                        }
                    }
                });
            }
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
        for (int i = 0; i < lists.size(); i++) {
            writer.startTAG("row");
            for (int j = 0; j < lists.get(i).size(); j++) {
                writer.startTAG("col");
                writer.attr("value", lists.get(i).get(j));
                writer.end();
            }
            writer.end();
        }
        writer.end();
    }
}