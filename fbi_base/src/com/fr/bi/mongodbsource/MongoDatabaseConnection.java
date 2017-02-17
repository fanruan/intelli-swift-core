package com.fr.bi.mongodbsource;

import com.fr.data.impl.AbstractDatabaseConnection;
import com.fr.data.impl.Connection;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;
import com.fr.stable.CodeUtils;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;
import com.mongodb.*;
import com.mongodb.client.MongoIterable;

import java.util.List;

/**
 * Created by richie on 16/1/22.
 */
public class MongoDatabaseConnection extends AbstractDatabaseConnection implements JSONParser {

    private static final long serialVersionUID = 565839783834368669L;
    //        mongodb://user1:password1@localhost/test
    MongoClientURI uri;
    private String url;
    private String username;
    private String password;
    private String defaultDatabaseName;
    private String options;

    public MongoDatabaseConnection() {

    }

    public MongoDatabaseConnection(String url,String userName ,String password) {
        this.url = url;
        this.username = userName;
        this.password = password;
        initURI();
        this.defaultDatabaseName = uri.getDatabase();
    }

    private void initURI() {
        String tmpuri = url;
        //    url for mangodb    "mongodb://192.168.3.246:27017/test";
//        if user and password not empty insert before ip
        if(StringUtils.isNotEmpty(username)){
            tmpuri = url.replace("mongodb://","mongodb://"+username+":"+password+"@");
        }
        uri = new MongoClientURI(tmpuri);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public MongoClientURI getUri() {
        return uri;
    }


    public String getUrl() {
        return url;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultDatabaseName() {
        return defaultDatabaseName;
    }

    public void setDefaultDatabaseName(String defaultDatabaseName) {
        this.defaultDatabaseName = defaultDatabaseName;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    public void testConnection() throws Exception {
        MongoClient client = createMongoClient();
        try {
            client.getConnectPoint();
        } catch (Exception e) {
            throw new Exception("Connect Failed");
        } finally {
            client.close();
        }
    }

    @Override
    public java.sql.Connection createConnection() throws Exception {
        return null;
    }

    @Override
    public String connectMessage(boolean status) {
        if (status) {
            return Inter.getLocText("Datasource-Connection_successfully") + "!";
        } else {
            return Inter.getLocText("Datasource-Connection_failed") + "!";
        }
    }

    @Override
    public void addConnection(List<String> list, String connectionName, Class<? extends Connection>[] acceptTypes) {
        for (Class<? extends Connection> accept : acceptTypes) {
            if (StableUtils.classInstanceOf(getClass(), accept)) {
                list.add(connectionName);
                break;
            }
        }
    }

    @Override
    public String getDriver() {
        return "mongodb.Driver.nouse";
    }

    @Override
    public String getOriginalCharsetName() {
        return null;
    }

    @Override
    public void setOriginalCharsetName(String s) {

    }

    @Override
    public String getNewCharsetName() {
        return null;
    }

    @Override
    public void setNewCharsetName(String s) {

    }

    public MongoClient createMongoClient() {
        return new MongoClient(uri);
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if ("MongoDatabaseAttr".equals(tagName)) {
                url = reader.getAttrAsString("url", StringUtils.EMPTY);
                username = reader.getAttrAsString("username", StringUtils.EMPTY);
                String pwd = reader.getAttrAsString("password", StringUtils.EMPTY);
                if (StringUtils.isNotEmpty(pwd)) {
                    password = CodeUtils.passwordDecode(pwd);
                }
                defaultDatabaseName = reader.getAttrAsString("defaultDB", StringUtils.EMPTY);
                options = reader.getAttrAsString("options", StringUtils.EMPTY);
                initURI();
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        super.writeXML(writer);
        writer.startTAG("MongoDatabaseAttr");
        writer.attr("url", url);
        writer.attr("username", username);
        if (StringUtils.isNotEmpty(password)) {
            writer.attr("password", CodeUtils.passwordEncode(password));
        }
        writer.attr("defaultDB", defaultDatabaseName);
        if (StringUtils.isNotEmpty(options)) {
            writer.attr("options", options);
        }
        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MongoDatabaseConnection cloned = (MongoDatabaseConnection) super.clone();
        cloned.url = url;
        cloned.username = username;
        cloned.password = password;
        cloned.defaultDatabaseName = defaultDatabaseName;
        cloned.options = options;
        return cloned;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {

    }

    @Override
    public String toString() {
        return "MongoDatabaseConnection{" +
                "uri=" + uri +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", defaultDatabaseName='" + defaultDatabaseName + '\'' +
                ", options='" + options + '\'' +
                '}';
    }
}