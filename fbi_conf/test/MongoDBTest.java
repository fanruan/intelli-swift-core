import com.fr.bi.common.inter.Traversal;
import com.fr.bi.data.MongoDBExtractor;
import com.fr.bi.conf.data.source.MongoDBTableSource;
import com.fr.bi.mongodb.MongoDatabaseConnection;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.PersistentTable;
import com.mongodb.*;
import com.mongodb.client.*;
import junit.framework.TestCase;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.*;

/**
 * Created by wang on 2017/1/18.
 */
public class MongoDBTest extends TestCase {
    public void testInsert() {
        try {
            String url = "mongodb://wang:1234@192.168.3.246:27017/test";
            MongoClientURI uri = new MongoClientURI(url);
            MongoClient mongo = new MongoClient(uri);
            MongoDatabase db = mongo.getDatabase("test");
            MongoCollection table = db.getCollection("employ");
            Document document = new Document();
            document.put("name", "wu");
            document.put("age", 33l);
            document.put("salary", 2000.5);
            document.put("gender", "M");
            document.put("marriage", true);
            document.put("createdDate", new Date());
            Document documentinside = new Document();
            documentinside.put("key", "pi");
            documentinside.put("value", 3.14);
            document.put("testColl", documentinside);

            Document documentinside2 = new Document();
            documentinside2.put("a", "a");
            documentinside2.put("b", 3.14);
            documentinside2.put("doc", documentinside);
            Document documentinside3 = new Document();
            documentinside3.put("a", "A");
            documentinside3.put("b", 3.15);
            documentinside2.put("doc", documentinside);

            List<Document> x = new ArrayList<Document>();
            x.add(documentinside2);
            x.add(documentinside3);
            document.put("array", x);

            List<String> x1 = new ArrayList<String>();
            x1.add("a");
            x1.add("b");
            document.put("array1", x1);


            table.insertOne(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Created successfully");
    }

    public void testGetCollections() {
        String url = "mongodb://wang:1234@192.168.3.246:27017/test";
        MongoClientURI uri = new MongoClientURI(url);
        MongoClient mongo = new MongoClient(uri);
        //第二：连接具体的数据库
        //其中参数是具体数据库的名称，若服务器中不存在，会自动创建
        MongoDatabase db = mongo.getDatabase("test");
        MongoIterable<String> colls = db.listCollectionNames();
        for (String s : colls) {
            System.out.println(s);
        }

    }

    public void testConnWithAuth() {
        ServerAddress address = new ServerAddress("192.168.3.246", 27017);
        String username = "wang";
        String password = "1234";
        String database = "test";
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClient client = new MongoClient(address, Arrays.asList(new MongoCredential[]{credential}));
        MongoDatabase db = client.getDatabase("test");
        MongoIterable<String> colls = db.listCollectionNames();
        for (String s : colls) {
            System.out.println(s);
        }
    }

    public void testGetRowCount() {
        String url = "mongodb://wang:1234@192.168.3.246:27017/test";
        MongoClientURI uri = new MongoClientURI(url);
        MongoClient mongo = new MongoClient(uri);
        MongoDatabase db = mongo.getDatabase("test");
        MongoCollection col2 = db.getCollection("employ");

        FindIterable iterable = col2.find();
        System.err.println("size: " + col2.count());
        MongoCursor cursor = iterable.iterator();
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toString());
        }
    }

    public void testGetAllFields() {
        String url = "mongodb://wang:1234@192.168.3.246:27017/test";
        MongoClientURI uri = new MongoClientURI(url);
        MongoClient mongo = new MongoClient(uri);
        MongoDatabase db = mongo.getDatabase("test");
        MongoCollection col2 = db.getCollection("employ");
        FindIterable iterable = col2.find();
        System.err.println("size: " + col2.count());
        MongoCursor cursor = iterable.iterator();
        String _id = "";
        while (cursor.hasNext()) {
            Document node = (Document) cursor.next();
//            dealWithDocument("",node);// 处理内嵌文档和数组
            dealWithDocument(node);//不处理

            System.out.println("\r\n");
        }
    }

    /**
     * 直接打印节点，不处理
     * @param node
     */
    private void dealWithDocument(Document node) {
        for (String key : node.keySet()) {
            System.out.println(key + " - " + node.get(key) + " type " + node.get(key).getClass().getSimpleName());
        }

    }

    /**
     * 把arrayList和内嵌Document都处理
     * @param pre
     * @param node
     */
    private void dealWithDocument(String pre, Document node) {
        String _id = "";
        for (String key : node.keySet()) {
            if (key.equals("_id")) {
                _id = node.get(key).toString();
            }
//                获取字段类型
            if (node.get(key).getClass().getSimpleName().equals("Document")) {
                Document nodeInside = (Document) node.get(key);
                dealWithDocument(pre + key + ".", nodeInside);
            } else if (node.get(key).getClass().getSimpleName().equals("ArrayList")) {
                List<Object> x = new ArrayList<Object>();
                x.addAll((Collection<?>) node.get(key));
                System.out.println("==============new table=================");
                for (int i = 0; i < x.size(); i++) {
                    Object a = x.get(i);
                    System.out.println("array_id " + _id);
                    if (a instanceof Document) {
                        dealWithDocument(pre + "array.", (Document) a);
                    } else {
                        System.out.println(pre + "array_idx " + (i + 1) + " type " + a.getClass().getSimpleName());
                    }
                }
                System.out.println("==============new table=================\r\n");

            } else {
                System.out.println(pre + "" + key + " - " + node.get(key) + " type " + node.get(key).getClass().getSimpleName());
            }
        }
    }

    public void testURLConnect() {
//        mongodb://wang:1234@user1:password1@localhost/test
        String url = "mongodb://wang:1234@192.168.3.246:27017/test";
        MongoClientURI uri = new MongoClientURI(url);
        System.err.println("uri " + uri.getURI());
        MongoClient mongo = new MongoClient(uri);
        MongoDatabase db = mongo.getDatabase("test");
        MongoCollection col2 = db.getCollection("employ");
        FindIterable iterable = col2.find();
        System.err.println("size: " + col2.count());
    }

    public void testQuery() {
        String url = "mongodb://wang:1234@192.168.3.246:27017/test";
        MongoClientURI uri = new MongoClientURI(url);
        MongoClient mongo = new MongoClient(uri);
        MongoDatabase db = mongo.getDatabase("test");
        MongoCollection col2 = db.getCollection("employ");
        String query = "{}";
        BsonDocument queryDocument = BsonDocument.parse(query);
        FindIterable iterable = col2.find(queryDocument);
        System.err.println("size: " + col2.count());
        MongoCursor cursor = iterable.iterator();
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toString());
        }
    }

    public void testReadFromMongoDB() {
        String url = "mongodb://192.168.3.246:27017/test";
        MongoDBTableSource mdts = new MongoDBTableSource();
        mdts.setDbName("test");
        mdts.setCollectionName("employ");
        mdts.setMdConnection(new MongoDatabaseConnection(url, "wang", "1234"));
        mdts.read(new Traversal< BIDataValue >(){
            @Override
            public void actionPerformed(BIDataValue data) {
                System.err.println("data "+ data.getValue());
            }
        } , null, null);
    }
    public void testGetPersistentTable() {
        MongoDBTableSource mdts = new MongoDBTableSource();
        String url = "mongodb://192.168.3.246:27017/test";
        mdts.setDbName("test");
        mdts.setCollectionName("employ");
        MongoDatabaseConnection mc = new MongoDatabaseConnection(url, "wang", "1234");
        mdts.setMdConnection(mc);
        PersistentTable table = MongoDBExtractor.getInstance().getCollection(mc, mc.getDefaultDatabaseName(), "employ");
        System.out.println(table.toString());
    }

    public void testGetTables() {
        MongoDBTableSource mdts = new MongoDBTableSource();
        String url = "mongodb://192.168.3.246:27017/test";
        mdts.setDbName("test");
        mdts.setCollectionName("employ");
        MongoDatabaseConnection mc = new MongoDatabaseConnection(url, "wang", "1234");
        mdts.setMdConnection(mc);
        List<String> table = MongoDBExtractor.getInstance().getCollections(mc);
        System.out.println(table.toString());
    }
}
