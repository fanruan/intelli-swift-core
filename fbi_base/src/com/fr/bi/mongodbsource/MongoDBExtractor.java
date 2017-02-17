package com.fr.bi.mongodbsource;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.stable.StringUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import org.bson.BsonDocument;
import org.bson.BsonUndefined;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * Created by wang on 2017/1/19.
 */
public class MongoDBExtractor {

    static class MongoDBExtractorHolder {
        static MongoDBExtractor instance = new MongoDBExtractor();
    }

    public static MongoDBExtractor getInstance() {
        return MongoDBExtractorHolder.instance;
    }


    public List<String> getCollections(MongoDatabaseConnection mc) {
        MongoClient client = null;
        List<String> collectList = new ArrayList<String>();
        try {
            client = mc.createMongoClient();
            MongoDatabase db = client.getDatabase(mc.getDefaultDatabaseName());
            MongoIterable<String> collectionNames = db.listCollectionNames();
            for (String collect : collectionNames) {
                collectList.add(collect);
            }
            return collectList;
        } catch (Exception ignore) {

        } finally {
            release(client);
        }
        return collectList;
    }

    public PersistentTable getCollection(MongoDatabaseConnection mc, String dbName, String collectionName) {
        PersistentTable dbTable = new PersistentTable("", collectionName, "");
        for (PersistentField field : getPersistentFields(mc, dbName, collectionName)) {
            dbTable.addColumn(field);
        }
        return dbTable;
    }

    private List<PersistentField> getPersistentFields(MongoDatabaseConnection mc, String dbName, String collectionName) {
        MongoClient client = null;
        List<PersistentField> fieldList = new ArrayList<PersistentField>();
        try {
            client = mc.createMongoClient();
            MongoDatabase db = client.getDatabase(dbName);
            MongoCollection cn = db.getCollection(collectionName);
            MongoCursor cursor = cn.find().limit(1).iterator();
            while (cursor.hasNext()) {
                Document node = (Document) cursor.next();
                fieldList.addAll(getFieldFromDocument("", node));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release(client);
        }
        return fieldList;
    }

    /**
     * @param prefix 内嵌文档应该在FieldName上加上父节点的名字，显示更易懂
     * @param doc
     * @return
     */
    private List<PersistentField> getFieldFromDocument(String prefix, Document doc) {
        List<PersistentField> fieldList = new ArrayList<PersistentField>();
        for (String key : doc.keySet()) {
            String type = doc.get(key).getClass().getName();
            if (doc.get(key) == null) {
                fieldList.add(new PersistentField(prefix + key, MongoTypeConvert.convertJavaType2SqlType("null")));
            } else if (type.equals(MongoConstants.MONGODB_DOCUMENT)) {
                fieldList.addAll(getFieldFromDocument(key + ".", (Document) doc.get(key)));
            } else if (type.equals(MongoConstants.MONGODB_ARRAY)) {
                BILoggerFactory.getLogger(this.getClass()).info("FineBI notSupport Array type in mongodb");
            } else {
                fieldList.add(new PersistentField(prefix + key, MongoTypeConvert.convertJavaType2SqlType(type)));
            }
        }
        return fieldList;
    }

    private int dealWithResultSet(final MongoCursor cursor, final Traversal<BIDataValue> travel) {
        int rowCount = 0;
        while (cursor.hasNext()) {
            Document node = (Document) cursor.next();
            extractDataFromDocument(node, rowCount, 0, travel);
            rowCount++;
        }
        return rowCount;
    }

    /**
     * 处理Document节点，arrayList不处理，内嵌文档深入处理
     * @param node
     * @param rowCount
     * @param colCount
     * @param travel
     * @return
     */
    private int extractDataFromDocument(Document node, int rowCount, int colCount, final Traversal<BIDataValue> travel) {
        Set<Map.Entry<String, Object>> set = node.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object nodeValue = entry.getValue();
            if (nodeValue instanceof Document) {
                colCount = extractDataFromDocument((Document) nodeValue, rowCount, colCount, travel);
            } else if (nodeValue instanceof java.util.ArrayList) {
//                TODO do nothing if array currently
            } else {
                Object val = MongoTypeConvert.dealWithMongoDBBasicObject(nodeValue);
                travel.actionPerformed(new BIDataValue(rowCount, colCount, val));
                colCount++;
            }
        }
        return colCount;
    }



    /**
     * @param mc
     * @param dbName
     * @param collectionName
     * @param travel
     * @param query
     * @param filter
     * @param sort
     * @return
     */
    public int extractData(MongoDatabaseConnection mc, String dbName, String collectionName, final Traversal<BIDataValue> travel, String query, String filter, String sort) {
        MongoClient client = null;
        try {
            client = mc.createMongoClient();
            MongoDatabase db = client.getDatabase(dbName);
            MongoCollection cn = db.getCollection(collectionName);
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
            MongoCursor cursor = iterable.iterator();
            int rowCount = dealWithResultSet(cursor, travel);
            return rowCount;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release(client);
        }
        return 0;
    }

    private void release(MongoClient client) {
        if (client != null) {
            client.close();
            client = null;
        }
    }

}
