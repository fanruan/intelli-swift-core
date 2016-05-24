package com.fr.bi.conf.data.source;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.base.FRContext;
import com.fr.base.TableData;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIExcelUtils;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.util.*;

/**
 * Created by GUY on 2015/3/2.
 */
public class ExcelTableSource extends AbstractTableSource implements JSONTransform {

    public static final String XML_TAG = "ExcelTableSource";
    /**
     *
     */
    private static final long serialVersionUID = -6619167463608584606L;
    private String fileName;
    @BICoreField
    private String fullFileName;
    @BICoreField
    private String[] fieldNames;

    private int[] columnTypes;

    public ExcelTableSource() {
        super();
    }


    private BIExcelTableData createExcelTableData() {
        return new BIExcelTableData(fullFileName, fieldNames, columnTypes);
    }

    @Override
    public IPersistentTable getPersistentTable() {
        if (dbTable == null) {
            dbTable = createBITable();
            BIExcelDataModel dm = null;
            try {
                dm = createExcelTableData().createDataModel();
                int cols = dm.getColumnCount();
                for (int i = 0; i < cols; i++) {

                    PersistentField column = new PersistentField(dm.getColumnName(i), dm.getColumnType(i), 255);
                    dbTable.addColumn(column);
                }
            } catch (Exception e) {
                FRContext.getLogger().error(e.getMessage(), e);
                dbTable = null;
            } finally {
                if (dm != null) {
                    try {
                        dm.release();
                    } catch (Exception e) {
                        FRContext.getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }

        return dbTable;
    }


    @Override
    public int getType() {
        return BIBaseConstant.TABLETYPE.EXCEL;
    }

    @Override
    public long read(final Traversal<BIDataValue> travel, BICubeFieldSource[] fields, ICubeDataLoader loader) {
        final BICubeFieldSource[] columns = fields;
        return BIExcelUtils.runExcel(createExcelTableData(), columns, new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                Object resValue = null;
                int i = v.getRow();
                int j = v.getCol();
                Object value = v.getValue();
                switch (columns[j].getFieldType()) {
                    case DBConstant.COLUMN.STRING:
                        resValue = value == null ? null : value.toString();
                        break;
                    case DBConstant.COLUMN.NUMBER:
                        if (value instanceof Double || value == null) {
                            resValue = value;
                        } else if (value instanceof Date) {
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(((Date) value).getTime());
                            resValue = (double) (c.get(Calendar.HOUR) * 3600 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND));
                        } else {
                            Double d = 0d;
                            try {
                                d = new Double(value.toString());
                            } catch (Exception ignore) {
                            }
                            resValue = d;
                        }
                        break;
                    default:
                        if (value == null) {
                            break;
                        }
                        if (value instanceof Date) {
                            try {
                                resValue = ((Date) value).getTime();
                            } catch (Exception e) {
                                resValue = null;
                            }
                            break;
                        }
                        resValue = DateUtils.string2Date(value.toString(), true).getTime();
                        break;
                }
                if (travel != null) {
                    travel.actionPerformed(new BIDataValue(i, j, resValue));
                }
            }
        });
    }

    /**
     * 获取某个字段的distinct值
     *
     * @param fieldName
     * @param userId
     */
    @Override
    public Set getFieldDistinctNewestValues(String fieldName, ICubeDataLoader loader, long userId) {
        ICubeFieldSource field = getFields().get(fieldName);
        final HashSet set = new HashSet();
        if (field == null) {
            return set;
        }
        BIExcelUtils.runExcel(createExcelTableData(), new ICubeFieldSource[]{field}, new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                set.add(data.getValue());
            }
        });
        return set;
    }

    @Override
    public JSONObject createPreviewJSON(ArrayList<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray fieldNames = new JSONArray();
        JSONArray values = new JSONArray();
        jo.put(BIJSONConstant.JSON_KEYS.FIELDS, fieldNames);
        jo.put(BIJSONConstant.JSON_KEYS.VALUE, values);
        BIExcelDataModel tableData = null;
        try {
            tableData = createExcelTableData().createDataModel();
            String[] columnNames = tableData.onlyGetColumnNames();
            int previewRowCount = Math.min(BIBaseConstant.PREVIEW_COUNT, tableData.getDataList().size());
            for (int col = 0; col < columnNames.length; col++) {
                String name = columnNames[col];
                if (!fields.isEmpty() && !fields.contains(name)) {
                    continue;
                }
                fieldNames.put(name);
                JSONArray value = new JSONArray();
                values.put(value);
                for (int row = 0; row < previewRowCount; row++) {
                    value.put(tableData.getValueAt4Preview(row, col));
                }
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            if (tableData != null) {
                tableData.release();
            }
        }
        return jo;
    }

    @Override
    public TableData createTableData(List<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        if (fields == null || fields.isEmpty()) {
            return createExcelTableData();
        }
        TableData td = null;
        BIExcelDataModel dataModel = null;
        try {
            dataModel = createExcelTableData().createDataModel();
            if (dataModel.getRowCount() == 0) {
                return createExcelTableData();
            }
            td = BIDBUtils.createTableData(fields, dataModel);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            if (dataModel != null) {
                dataModel.release();
            }
        }
        return td;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("connection_name", DBConstant.CONNECTION.EXCEL_CONNECTION);
        jo.put("full_file_name", fullFileName);
        jo.put("table_name", fileName);
        return jo;
    }

    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("full_file_name")) {
            fullFileName = jo.getString("full_file_name");
        }
        if (jo.has("table_name")) {
            fileName = jo.getString("table_name");
        }
        if (jo.has("fields")) {
            JSONArray fields = jo.getJSONArray("fields").getJSONArray(0);
            fieldNames = new String[fields.length()];
            columnTypes = new int[fields.length()];
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                fieldNames[i] = field.getString("field_name");
                columnTypes[i] = BIDBUtils.biTypeToSql(field.getInt("field_type"));
            }
        }
    }

//    @Override
//    public void readXML(XMLableReader reader) {
//        super.readXML(reader);
//        this.fields.clear();
//        this.fullFileName = reader.getAttrAsString("fullname", StringUtils.EMPTY);
//        this.fileName = reader.getAttrAsString("tablename", StringUtils.EMPTY);
//        final List<String> fNames = new ArrayList<String>();
//        final List<Integer> ftypes = new ArrayList<Integer>();
//        reader.readXMLObject(new XMLReadable() {
//            @Override
//            public void readXML(XMLableReader reader) {
//                if (reader.isChildNode()) {
//                    if (reader.getTagName().equals(BIField.XML_TAG)) {
//                        DBField field = DBField.getBiEmptyField();
//                        reader.readXMLObject(field);
//                        fields.put(field.getFieldName(), field);
//                    } else if (ComparatorUtils.equals(reader.getTagName(), "fieldname")) {
//                        fNames.add(reader.getAttrAsString("value", StringUtils.EMPTY));
//                    } else if (ComparatorUtils.equals(reader.getTagName(), "fieldtype")) {
//                        ftypes.add(reader.getAttrAsInt("value", 0));
//                    }
//                }
//            }
//        });
//        fieldNames = fNames.toArray(new String[fNames.size()]);
//
//        fieldTypes = new int[ftypes.size()];
//        for (int i = 0; i < fieldTypes.length; i++) {
//            fieldTypes[i] = ftypes.get(i);
//        }
//    }
//
//    @Override
//    public void writeXML(XMLPrintWriter writer) {
//        writer.startTAG(XML_TAG)
//                .attr("tablename", fileName)
//                .attr("fullname", fullFileName)
//                .attr("md5", fetchObjectCore().getID().getIdentityValue());
//        Set<String> key = fields.keySet();
//        for (int i = 0; i < fieldNames.length; i++) {
//            writer.startTAG("fieldname");
//            writer.attr("value", fieldNames[i]);
//            writer.end();
//        }
//        for (int i = 0; i < fieldTypes.length; i++) {
//            writer.startTAG("fieldtype");
//            writer.attr("value", fieldTypes[i]);
//            writer.end();
//        }
//        Iterator<String> keyIter = key.iterator();
//        while (keyIter.hasNext()) {
//            fields.get(keyIter.next()).writeXML(writer);
//
//        }
//        writer.end();
//    }
}