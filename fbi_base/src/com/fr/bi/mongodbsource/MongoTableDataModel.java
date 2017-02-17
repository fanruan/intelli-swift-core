package com.fr.bi.mongodbsource;

import com.fr.base.TableData;
import com.fr.general.ModuleContext;
import com.fr.general.data.DataModel;
import com.fr.general.data.TableDataException;
import com.fr.stable.StringUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richie on 16/1/22.
 */
public class MongoTableDataModel implements DataModel {

    private static final long serialVersionUID = -1603137209246564914L;
    private MongoClient client;
    private List<String> columnNames;
    private List<List<Object>> data;


    public MongoTableDataModel(MongoDatabaseConnection mc, String dbName, String tableName, String query, String filter, String sort, int rowCount) {
        initData(mc, dbName, tableName, query, filter, sort, rowCount);
    }


    private synchronized void initData(MongoDatabaseConnection mc, String dbName, String tableName, String query, String filter, String sort, int rowCount) {
        if (client == null) {
            client = mc.createMongoClient();
            MongoDatabase db = client.getDatabase(dbName);
            MongoCollection cn = db.getCollection(tableName);
            if (StringUtils.isEmpty(query)) {
                query = "{}";
            }
            BsonDocument queryDocument = BsonDocument.parse(query);
            FindIterable iterable = cn.find(queryDocument);
            if (StringUtils.isNotBlank(filter)) {
                BsonDocument filterDocument = BsonDocument.parse(filter);
                iterable = iterable.filter(filterDocument);
            }
            if (StringUtils.isNotBlank(sort)) {
                BsonDocument sortDocument = BsonDocument.parse(sort);
                iterable = iterable.sort(sortDocument);
            }
            MongoCursor cursor;
            if (rowCount != TableData.RESULT_ALL) {
                if (rowCount == TableData.RESULT_NOT_NEED) {
                    cursor = iterable.limit(1).iterator();
                } else {
                    cursor = iterable.limit(rowCount).iterator();
                }
            } else {
                cursor = iterable.iterator();
            }
            columnNames = new ArrayList<String>();
            data = new ArrayList<List<Object>>();
            boolean colGet = false;
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                if (!colGet) {
                    for (String column : doc.keySet()) {
                        columnNames.add(column);
                        colGet = true;
                    }
                }
                List<Object> rowData = new ArrayList<Object>();
                for (String name : columnNames) {
                    rowData.add(doc.get(name));
                }
                data.add(rowData);
            }

        }
    }

    public MongoCursor getMongoCursor() {
        return null;
    }

    @Override
    public int getColumnCount() throws TableDataException {
        return columnNames == null ? 0 : columnNames.size();
    }

    @Override
    public int getColumnIndex(String s) throws TableDataException {
//        TODO
        return 0;
    }

    @Override
    public String getColumnName(int columnIndex) throws TableDataException {
        return columnNames == null ? null : columnNames.get(columnIndex);
    }

    @Override
    public boolean hasRow(int rowIndex) throws TableDataException {
        return data != null && data.size() > rowIndex;
    }

    @Override
    public int getRowCount() throws TableDataException {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) throws TableDataException {
        if (data != null && data.size() > rowIndex) {
            List<Object> rowData = data.get(rowIndex);
            if (rowData != null && rowData.size() > columnIndex) {
                return rowData.get(columnIndex);
            }
        }
        return null;
    }

    @Override
    public void release() throws Exception {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}