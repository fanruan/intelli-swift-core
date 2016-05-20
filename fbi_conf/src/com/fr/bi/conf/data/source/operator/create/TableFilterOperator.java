package com.fr.bi.conf.data.source.operator.create;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by GUY on 2015/3/5.
 */
public class TableFilterOperator extends AbstractCreateTableETLOperator {

    public static final String XML_TAG = "TableFilterOperator";
    private static final long serialVersionUID = 2511687396196080399L;
    @BICoreField
    private List<FieldState> fieldStates = new ArrayList<FieldState>();


    public TableFilterOperator(long userId) {
        super(userId);
    }

    public TableFilterOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();

        for (FieldState fieldState : fieldStates) {
            ja.put(fieldState.createJSON());
        }
        jo.put("fields_state", ja);
        return jo;
    }


    /**
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        JSONArray ja = jo.getJSONArray("fields_state");

        for (int i = 0; i < ja.length(); i++) {
            FieldState fieldState = new FieldState();
            fieldState.parseJSON(ja.getJSONObject(i));
            fieldStates.add(fieldState);
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
            if (FieldState.XML_TAG.equals(reader.getTagName())) {
                FieldState fieldState = new FieldState();
                reader.readXMLObject(fieldState);
                fieldStates.add(fieldState);
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
        Iterator<FieldState> it = fieldStates.iterator();
        while (it.hasNext()) {
            FieldState fieldState = it.next();

            fieldState.writeXML(writer);
        }
        writer.end();
    }


    @Override
    public DBTable getBITable(DBTable[] tables) {
        DBTable DBTable = getBITable();

        for (int i = 0; i < tables.length; i++) {
            Iterator<FieldState> it = fieldStates.iterator();
            while (it.hasNext()) {
                FieldState fieldState = it.next();

                if (fieldState.isChecked()) {
                    BIColumn c = tables[i].getBIColumn(fieldState.getFieldName());
                    DBTable.addColumn(c);
                }
            }
        }
        return DBTable;
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents));
        return ti.getRowCount();
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents), start, end);
        return ti.getRowCount();
    }


    public class FieldState implements JSONTransform, XMLable {
        public static final String XML_TAG = "FieldState";
        private String field_name;
        private boolean state;

        public String getFieldName() {
            return field_name;
        }

        /**
         * 判断是否选中
         *
         * @return 是否选中
         */
        public boolean isChecked() {
            return state;
        }

        /**
         * 创建json
         *
         * @return json对象
         * @throws Exception 报错
         */
        @Override
        public JSONObject createJSON() throws Exception {
            JSONObject jo = new JSONObject();
            jo.put("field_name", field_name);
            jo.put("checked", state);

            return jo;
        }

        /**
         * 解析json
         *
         * @param jo json对象
         * @throws Exception 报错
         */
        @Override
        public void parseJSON(JSONObject jo) throws Exception {
            if (jo.has("field_name")) {
                field_name = jo.getString("field_name");
            }

            if (jo.has("checked")) {
                state = jo.getBoolean("checked");
            }
        }

        @Override
        public void readXML(XMLableReader reader) {
            if (reader.isAttr()) {
                field_name = reader.getAttrAsString("field_name", StringUtils.EMPTY);
                state = reader.getAttrAsBoolean("state", false);
            }
        }

        @Override
        public void writeXML(XMLPrintWriter writer) {
            writer.startTAG(XML_TAG);
            writer.attr("field_name", field_name);
            writer.attr("state", state);
            writer.end();
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            FieldState fieldState = new FieldState();
            fieldState.field_name = new String(this.field_name);
            fieldState.state = this.state;

            return fieldState;
        }

        public boolean equals(FieldState fieldState) {
            return ComparatorUtils.equals(this.field_name, fieldState.getFieldName());
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("FieldState{");
            sb.append("field_name='").append(field_name).append('\'');
            sb.append(", state=").append(state);
            sb.append('}');
            return sb.toString();
        }
    }
}