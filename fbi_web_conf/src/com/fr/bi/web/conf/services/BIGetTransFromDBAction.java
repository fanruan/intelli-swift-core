package com.fr.bi.web.conf.services;

import com.fr.base.TableData;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.file.DatasourceManager;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.data.DataModel;
import com.fr.general.data.TableDataException;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class BIGetTransFromDBAction extends AbstractBIConfigureAction {

    private static String[] TRANS_TABLE_FIELDS = new String[]{
            "connection_name", "schema_name", "table_name", "translated_table_name"
    };
    private static String[] TRANS_FIELD_FIELDS = new String[]{
            "connection_name", "schema_name", "table_name", "field_name", "translated_field_name"
    };
    private static final String SERVER_TABLE_NAME = "__bi_translated_table_names__";
    private static final String SERVER_FIELD_NAME = "__bi_translated_field_names__";

    @Override
    public String getCMD() {
        return "get_trans_from_db";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String specifiedSource = WebUtils.getHTTPRequestParameter(req, BIJSONConstant.JSON_KEYS.TYPE);

        if (ComparatorUtils.equals(specifiedSource, DBConstant.TRANS_TYPE.READ_FROM_DB)) {
            readFromDB(req, res, userId);
        } else if (ComparatorUtils.equals(specifiedSource, DBConstant.TRANS_TYPE.READ_FROM_TABLEDATA)) {
            readFromTABLEDATA(req, res, userId);
        } else if (checkExistOfEmbbededTableData()) {
            ask4SpecifiedSource(res);
        } else {
            readFromDB(req, res, userId);
        }
    }


    private Map<String, DBTableSource> getDBSource(String tableJO, long userId) throws Exception {
        JSONObject jo = new JSONObject(tableJO);
        Iterator<String> iterator = jo.keys();
        Map<String, DBTableSource> sources = new HashMap<String, DBTableSource>();
        while (iterator.hasNext()){
            String id = iterator.next();
            ITableSource source = TableSourceFactory.createTableSource(jo.getJSONObject(id), userId);
            if (source.getType() == BIBaseConstant.TABLETYPE.DB){
                sources.put(id, (DBTableSource)source);
            }
        }
        return sources;
    }


    private void ask4SpecifiedSource(HttpServletResponse res) throws Exception {
        WebUtils.printAsString(res, DBConstant.TRANS_TYPE.CHOOSE);
    }

    private void readFromDB(HttpServletRequest req, HttpServletResponse res, long userId) throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject tableTrans = new JSONObject();
        JSONObject fieldTrans = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.TABLE, tableTrans);
        jo.put(BIJSONConstant.JSON_KEYS.FIELD, fieldTrans);
        String tableJsonString = WebUtils.getHTTPRequestParameter(req, BIJSONConstant.JSON_KEYS.TABLES);
        Iterator<Map.Entry<String, DBTableSource>> it = getDBSource(tableJsonString, userId).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DBTableSource> entry = it.next();
            DBTable table = entry.getValue().getDbTable();
            if (!StringUtils.isEmpty(table.getRemark())) {
                tableTrans.put(entry.getKey(), table.getRemark());
            }
            for (BIColumn column : table.getColumnArray()) {
                if (!StringUtils.isEmpty(column.getRemark())) {
                    fieldTrans.put(entry.getKey() + column.getFieldName(), column.getRemark());
                }
            }
        }
        WebUtils.printAsJSON(res, jo);
    }

    private void readFromTABLEDATA(HttpServletRequest req, HttpServletResponse res, long userId) throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject trans = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.VALUE, trans);
        String tableJsonString = WebUtils.getHTTPRequestParameter(req, BIJSONConstant.JSON_KEYS.TABLES);
        Map<String, DBTableSource> sourceMap = getDBSource(tableJsonString, userId);
        TableData translatedTableNameTableData = DatasourceManager.getInstance().getTableData(SERVER_TABLE_NAME);
        TableData translatedFieldNameTableData = DatasourceManager.getInstance().getTableData(SERVER_FIELD_NAME);
        if (translatedTableNameTableData != null && translatedFieldNameTableData != null) {
            Calculator c = Calculator.createCalculator();
            DataModel model4TableName = translatedTableNameTableData.createDataModel(c);
            DataModel model4FieldName = translatedFieldNameTableData.createDataModel(c);
            addTableTrans(sourceMap, model4TableName, jo);
            addFieldTrans(sourceMap, model4FieldName, jo);
        }

        WebUtils.printAsJSON(res, jo);
    }

    private void addTableTrans(Map<String, DBTableSource> sourceMap, DataModel model4TableName, JSONObject jo) throws Exception {
        JSONObject tableTrans = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.TABLE, tableTrans);
        for (int i = 0; i < model4TableName.getRowCount(); i++) {
            Object connectionOb = model4TableName.getValueAt(i, 0);
            Object schemaOb = model4TableName.getValueAt(i, 1);
            Object tableOb = model4TableName.getValueAt(i, 2);
            Object translatedTableOb = model4TableName.getValueAt(i, 3);
            String id = getIdFromSource(sourceMap, connectionOb, schemaOb, tableOb);
            if (!StringUtils.isEmpty(id) && translatedTableOb != null) {
                tableTrans.put(id, translatedTableOb.toString());
            }
        }
    }

    private void addFieldTrans(Map<String, DBTableSource> sourceMap, DataModel model4FieldName, JSONObject jo) throws Exception {
        JSONObject fieldTrans = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.FIELD, fieldTrans);
        for (int i = 0; i < model4FieldName.getRowCount(); i++) {
            Object connectionOb = model4FieldName.getValueAt(i, 0);
            Object schemaOb = model4FieldName.getValueAt(i, 1);
            Object tableOb = model4FieldName.getValueAt(i, 2);
            Object fieldOb = model4FieldName.getValueAt(i, 3);
            Object translatedTableOb = model4FieldName.getValueAt(i, 4);
            String id = getIdFromSource(sourceMap, connectionOb, schemaOb, tableOb);
            if (!StringUtils.isEmpty(id) && translatedTableOb != null && fieldOb != null) {

                fieldTrans.put(id + fieldOb, translatedTableOb.toString());
            }
        }
    }

    private String getIdFromSource(Map<String, DBTableSource> sourceMap, Object connectionOb, Object schemaOb, Object tableOb) {
        if (connectionOb == null || tableOb == null) {
            return null;
        }
        String dbName = connectionOb.toString();
        String tableName = tableOb.toString();
        Iterator<Map.Entry<String, DBTableSource>> it = sourceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DBTableSource> entry = it.next();
            if (ComparatorUtils.equals(entry.getValue(), new DBTableSource(dbName, tableName))) {
                return entry.getKey();
            }
        }
        return null;
    }


    private boolean checkExistOfEmbbededTableData() throws TableDataException {
        TableData translatedTableNameTableData = DatasourceManager.getInstance().getTableData(SERVER_TABLE_NAME);
        TableData translatedFieldNameTableData = DatasourceManager.getInstance().getTableData(SERVER_FIELD_NAME);
        if (translatedTableNameTableData != null && translatedFieldNameTableData != null) {
            Calculator c = Calculator.createCalculator();
            DataModel model4TableName = translatedTableNameTableData.createDataModel(c);
            DataModel model4FieldName = translatedFieldNameTableData.createDataModel(c);
            Set tableSet = new HashSet();
            for (int ci = 0, clen = model4TableName.getColumnCount(); ci < clen; ci++) {
                String cName = model4TableName.getColumnName(ci);
                if (cName != null) {
                    tableSet.add(cName.toLowerCase());
                }
            }
            for (int ci = 0; ci < TRANS_TABLE_FIELDS.length; ci++) {
                if (!tableSet.contains(TRANS_TABLE_FIELDS[ci])) {
                    return false;
                }
            }

            Set fieldSet = new HashSet();
            for (int ci = 0, clen = model4FieldName.getColumnCount(); ci < clen; ci++) {
                String cName = model4FieldName.getColumnName(ci);
                if (cName != null) {
                    fieldSet.add(cName.toLowerCase());
                }
            }
            for (int ci = 0; ci < TRANS_FIELD_FIELDS.length; ci++) {
                if (!fieldSet.contains(TRANS_FIELD_FIELDS[ci])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

}