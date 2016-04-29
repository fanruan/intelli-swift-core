package com.fr.bi.conf.data.source;

import com.fr.base.TableData;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.db.ServerLinkInformation;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.DecryptBi;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.util.Set;

/**
 * Created by GUY on 2015/3/2.
 */
public class SQLTableSource extends ServerTableSource {

    public static final String XML_TAG = "SQLTableSource";

    @BICoreField
    private String sql;
    @BICoreField
    private String sqlConnection;
    @BICoreField
    private String sqlName;

    public SQLTableSource() {
        super();
    }

    public String getQuery() {
        return sql;
    }

    public String getSqlConnection() {
        return sqlConnection;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("sql", DecryptBi.encrypt(sql, "sh"));
        jo.put("dataLinkName", sqlConnection);
        jo.put("connection_name", DBConstant.CONNECTION.SQL_CONNECTION);
        jo.put("table_name", sqlName);
        return jo;
    }

    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if(jo.has("sql")){
            sql = DecryptBi.decrypt(jo.getString("sql"), "sh");
        }
        if(jo.has("dataLinkName")){
            sqlConnection = jo.getString("dataLinkName");
        }
        if(jo.has("table_name")){
            sqlName = jo.getString("table_name");
        }
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        this.sql = reader.getAttrAsString("sql", StringUtils.EMPTY);
        this.sqlConnection = reader.getAttrAsString("sql_connection", StringUtils.EMPTY);
        this.sqlName = reader.getAttrAsString("table_name", StringUtils.EMPTY);
        this.fields.clear();
        reader.readXMLObject(new XMLReadable() {
            @Override
            public void readXML(XMLableReader reader) {
                if (reader.isChildNode()) {
                    if (reader.getTagName().equals(BIField.XML_TAG)) {
                        DBField field = DBField.getBiEmptyField();
                        reader.readXMLObject(field);
                        fields.put(field.getFieldName(), field);
                    }
                }
            }
        });
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {

        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        writer.attr("sql", sql)
                .attr("sql_connection", sqlConnection)
                .attr("tablename", sqlName);
        Set<String> key = fields.keySet();
        for (String aKey : key) {
            fields.get(aKey).writeXML(writer);
        }
        writer.end();
    }

    @Override
    public DBTable getDbTable() {
        dbTable = BIDBUtils.getServerBITable(sqlConnection, sql, fetchObjectCore().getID().getIdentityValue());
        return dbTable;
    }

    @Override
    protected TableData getTableData() {
        try {
            ServerLinkInformation serverLinkInformation = new ServerLinkInformation(getSqlConnection(), getQuery());
            return serverLinkInformation.createDBTableData();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public int getType() {
        return BIBaseConstant.TABLETYPE.SQL;
    }
}