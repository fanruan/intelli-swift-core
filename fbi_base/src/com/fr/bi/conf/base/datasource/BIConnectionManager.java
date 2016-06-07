package com.fr.bi.conf.base.datasource;

import com.fr.base.FRContext;
import com.fr.bi.stable.data.db.DataLinkInformation;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.data.core.DataCoreUtils;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dialect.MSSQLDialect;
import com.fr.data.core.db.dialect.OracleDialect;
import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.file.XMLFileManager;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2016/3/18.
 */
public class BIConnectionManager extends XMLFileManager {
    private static final String XML_TAG = "BIConnectionManager";
    private Map<String, BIConnection> connMap = new ConcurrentHashMap<String, BIConnection>();

    private static BIConnectionManager manager;

    private BIConnectionManager() {

    }

    public static BIConnectionManager getInstance() {
        synchronized (BIConnectionManager.class) {
            if (manager == null) {
                manager = new BIConnectionManager();
                manager.readXMLFile();
            }
            return manager;
        }
    }

    public String getSchema(String name) {
        if (connMap.containsKey(name)) {
            return connMap.get(name).getSchema();
        }
        Connection connection = DatasourceManager.getInstance().getConnection(name);
        if (needSchema(connection)) {
            String[] schemas = DataCoreUtils.getDatabaseSchema(connection);
            connMap.put(name, new BIConnection(name, schemas != null && schemas.length != 0 ? schemas[0] : StringUtils.EMPTY));
        } else {
            connMap.put(name, new BIConnection(name, null));
        }
        return null;
    }

    public Connection getConnection(String name) {
        return DatasourceManager.getInstance().getConnection(name);
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                BIConnectionManager.getInstance().envChanged();
            }
        });
    }

    public void envChanged() {
        readXMLFile();
    }

    @Override
    public String fileName() {
        return "biconnection.xml";
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            if (ComparatorUtils.equals(reader.getTagName(), "conn")) {
                BIConnection connection = new BIConnection(reader.getAttrAsString("name", StringUtils.EMPTY), reader.getAttrAsString("schema", null));
                connMap.put(connection.getName(), connection);
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        for (BIConnection connection : connMap.values()) {
            writer.startTAG("conn");
            writer.attr("name", connection.getName());
            if (connection.getSchema() != null) {
                writer.attr("schema", connection.getSchema());
            }
            writer.end();
        }
        writer.end();
    }

    public void updateConnection(String linkData, String oldName) throws Exception {
        JSONObject linkDataJo = new JSONObject(linkData);
        String newName = linkDataJo.optString("name");
        DataLinkInformation dl = new DataLinkInformation();
        dl.parseJSON(linkDataJo);
        JDBCDatabaseConnection jdbcDatabaseConnection = dl.createJDBCDatabaseConnection();
        BIConnectOptimizationUtils utils = BIConnectOptimizationUtilsFactory.getOptimizationUtils(jdbcDatabaseConnection);
        jdbcDatabaseConnection = utils.optimizeConnection(jdbcDatabaseConnection);
        DatasourceManagerProvider datasourceManager = DatasourceManager.getInstance();

        if (!ComparatorUtils.equals(oldName, newName)) {
            datasourceManager.renameConnection(oldName, newName);
        }
        BIDBUtils.dealWithJDBCConnection(jdbcDatabaseConnection);
        datasourceManager.putConnection(newName, jdbcDatabaseConnection);
        connMap.remove(oldName);
        connMap.put(newName, new BIConnection(newName, linkDataJo.optString("schema")));
        try {
            FRContext.getCurrentEnv().writeResource(datasourceManager);
            FRContext.getCurrentEnv().writeResource(this);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void removeConnection(String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        connMap.remove(name);
        DatasourceManagerProvider datasourceManager = DatasourceManager.getInstance();
        Iterator<String> nameIt = datasourceManager.getConnectionNameIterator();
        while (nameIt.hasNext()) {
            String connectionName = nameIt.next();
            if (ComparatorUtils.equals(name, connectionName)) {
                datasourceManager.removeConnection(name);
                break;
            }
        }

        try {
            FRContext.getCurrentEnv().writeResource(datasourceManager);
            FRContext.getCurrentEnv().writeResource(this);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    private boolean testConnection(Connection c) {
        try {
            c.testConnection();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public JSONObject createJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        DatasourceManagerProvider datasourceManager = DatasourceManager.getInstance();
        Iterator<String> nameIt = datasourceManager.getConnectionNameIterator();

        int index = 0;
        while (nameIt.hasNext()) {
            String name = nameIt.next();
            JDBCDatabaseConnection c = datasourceManager.getConnection(name, JDBCDatabaseConnection.class);
            if (c != null && testConnection(c)) {
                if (isMicrosoftAccessDatabase(c)) {
                    continue;
                }
                JSONObject jo = new JSONObject();
                jo.put("name", name);
                jo.put("driver", c.getDriver());
                jo.put("url", c.getURL());
                jo.put("user", c.getUser());
                jo.put("password", c.getPassword());
                jo.put("originalCharsetName", StringUtils.alwaysNotNull(c.getOriginalCharsetName()));
                jo.put("newCharsetName", StringUtils.alwaysNotNull(c.getNewCharsetName()));
                jo.put("schema", getSchema(name));
                jsonObject.put("link" + index++, jo);
            }
        }

        return jsonObject;
    }

    private boolean isMicrosoftAccessDatabase(JDBCDatabaseConnection c) {
        return "sun.jdbc.odbc.JdbcOdbcDriver".equals(c.getDriver()) && c.getURL().indexOf("Microsoft Access Driver") > 0;
    }

    private boolean needSchema(Connection c) {
        java.sql.Connection conn = null;
        try {
            conn = c.createConnection();
            Dialect dialcet = DialectFactory.generateDialect(conn, c.getDriver());
            return dialcet instanceof OracleDialect || dialcet instanceof MSSQLDialect;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            DBUtils.closeConnection(conn);
        }
        return false;
    }
}
