package com.fr.bi.conf.data.source;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.TableData;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.data.MongoDBExtractor;
import com.fr.bi.mongodb.MongoDatabaseConnection;
import com.fr.bi.mongodb.MongoTableData;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.general.data.DataModel;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wang on 2017/1/18.
 */
public class MongoDBTableSource extends AbstractTableSource {
    public static final String XML_TAG = "MongoDBTableSource";
    private static final long serialVersionUID = 7701170786148096356L;
    //库的连接名
    @BICoreField
    protected String dbName;
    //表
    @BICoreField
    protected String collectionName;
    @BIIgnoreField
    protected MongoDatabaseConnection mdConnection;
    private String query;
    private String filter;

    private String sort;

    @Override
    public JSONObject createPreviewJSON(ArrayList<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        JSONObject jo = new JSONObject();
        EmbeddedTableData emTableData = null;
        DataModel dm = null;
        try {
            emTableData = EmbeddedTableData.embedify(createPreviewTableData(), null, BIBaseConstant.PREVIEW_COUNT);
            dm = emTableData.createDataModel(null,BIBaseConstant.PREVIEW_COUNT);
            JSONArray fieldNames = new JSONArray();
            JSONArray values = new JSONArray();
            JSONArray fieldTypes = new JSONArray();
            jo.put(BIJSONConstant.JSON_KEYS.FIELDS, fieldNames);
            jo.put(BIJSONConstant.JSON_KEYS.VALUE, values);
            jo.put(BIJSONConstant.JSON_KEYS.TYPE, fieldTypes);
            int colLen = dm.getColumnCount();
            int rolLen = Math.min(dm.getRowCount(), BIBaseConstant.PREVIEW_COUNT);
            Map<String, ICubeFieldSource> fieldsMap = getFields();

            for (int col = 0; col < colLen; col++) {
                String name = dm.getColumnName(col);
                if (!fields.isEmpty() && !fields.contains(name)) {
                    continue;
                }
                int fieldType = fieldsMap.get(name).getFieldType();
                fieldNames.put(name);
                fieldTypes.put(fieldType);
                JSONArray value = new JSONArray();
                values.put(value);
                for (int row = 0; row < rolLen; row++) {
                    boolean isString = false;
                    if (fieldsMap.containsKey(name) &&  fieldType == DBConstant.COLUMN.STRING) {
                        isString = true;
                    }
                    Object val = dm.getValueAt(row, col);
                    value.put((isString && val == null) ? "" : val);
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), "table preview failed!");
            return jo;
        } finally {
            if (null != dm) {
                dm.release();
            }
            if (null != emTableData) {
                emTableData.clear();
            }
        }
        return jo;
    }

    private void dealWithOneData(Traversal<BIDataValue> travel, BIDataValue v) {
        if (v != null) {
            travel.actionPerformed(v);
        }
    }
    protected TableData createPreviewTableData() throws Exception {
        MongoDatabaseConnection connection =(MongoDatabaseConnection) BIConnectionManager.getBIConnectionManager().getConnection(dbName);
        String query = "";
        mdConnection = connection;
        return new MongoTableData(connection,connection.getDefaultDatabaseName(),collectionName ,query);
    }

    @Override
    public int getType() {
        return BIBaseConstant.TABLETYPE.DB;
    }

    @Override
    public long read(final Traversal<BIDataValue> travel, final ICubeFieldSource[] fields, ICubeDataLoader loader) {
        long rowCount = 0;
        try {
            rowCount = MongoDBExtractor.getInstance().extractData(mdConnection, mdConnection.getDefaultDatabaseName(), collectionName, new Traversal<BIDataValue>() {
                @Override
                public void actionPerformed(BIDataValue v) {
                    try {
                        dealWithOneData(travel, v);
                    } catch (Exception e) {
                        BILoggerFactory.getLogger().error(e.getMessage(), e);
                    }
                }
            }, query, filter, sort);
        } catch (Throwable e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
        }
        return rowCount;
    }

    @Override
    public IPersistentTable getPersistentTable() {
        if (dbTable == null) {
            mdConnection = (MongoDatabaseConnection) BIConnectionManager.getBIConnectionManager().getConnection(dbName);
            dbTable = MongoDBExtractor.getInstance().getCollection(mdConnection, mdConnection.getDefaultDatabaseName(), collectionName);
        }
        return dbTable;
    }
    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("connection_name", dbName);
        jo.put("table_name", collectionName);
        return jo;
    }
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("connection_name")) {
            dbName = jo.getString("connection_name");
        }
        if (jo.has("table_name")) {
            collectionName = jo.getString("table_name");
        }

    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setMdConnection(MongoDatabaseConnection mdConnection) {
        this.mdConnection = mdConnection;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
