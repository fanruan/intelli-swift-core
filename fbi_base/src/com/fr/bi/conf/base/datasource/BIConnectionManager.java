package com.fr.bi.conf.base.datasource;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.data.db.DataLinkInformation;
import com.fr.data.core.DataCoreUtils;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dialect.MSSQLDialect;
import com.fr.data.core.db.dialect.OracleDialect;
import com.fr.data.impl.Connection;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.file.XMLFileManager;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2016/3/18.
 */
public class BIConnectionManager extends XMLFileManager implements BIConnectionProvider {
    public static final String XML_TAG = "BIConnectionManager";
    private Map<String, BIConnection> connMap = new ConcurrentHashMap<String, BIConnection>();
    private Map<String, Connection> availableConnection = new HashMap<String, Connection>();
    private static BIConnectionManager manager;
    private static BILogger logger = BILoggerFactory.getLogger(BIConnectionManager.class);

    private BIConnectionManager() {

    }

    @Override
    public void updateAvailableConnection() {
        availableConnection.clear();
        DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
        Iterator<String> nameIt = datasourceManager.getConnectionNameIterator();
        while (nameIt.hasNext()) {
            String name = nameIt.next();
            Connection c = datasourceManager.getConnection(name);
            if (c != null && isConnectionAvailable()) {
                availableConnection.put(name, c);
            }
        }
    }

    public static BIConnectionProvider getBIConnectionManager() {
        return StableFactory.getMarkedObject(BIConnectionProvider.XML_TAG, BIConnectionProvider.class);
    }

    public static BIConnectionManager getInstance() {
        synchronized (BIConnectionManager.class) {
            if (manager == null) {
                manager = new BIConnectionManager();
                manager.readXMLFile();
                manager.updateAvailableConnection();
            }
            return manager;
        }
    }

    @Override
    public String getSchema(String name) {
        if (connMap.containsKey(name)) {
            return connMap.get(name).getSchema();
        }
        Connection connection = DatasourceManager.getProviderInstance().getConnection(name);
        if (idNeedSchema(connection)) {
            String[] schemas = DataCoreUtils.getDatabaseSchema(connection);
            connMap.put(name, new BIConnection(name, schemas != null && schemas.length != 0 ? schemas[0] : StringUtils.EMPTY));
        } else {
            connMap.put(name, new BIConnection(name, null));
        }
        try {
            FRContext.getCurrentEnv().writeResource(this);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return StringUtils.EMPTY;
    }

    private long getCreateBy(String name, long userId) {
        if (connMap.containsKey(name)) {
            return connMap.get(name).getCreateBy();
        }
        return userId;
    }

    private long getInitTime(String name) {
        if (connMap.containsKey(name)) {
            long initTime = connMap.get(name).getInitTime();
            if (ComparatorUtils.equals(initTime, 0)) {
                ensureInitTimeExist();
                return getInitTime(name);
            }
            return initTime;
        }
        return System.currentTimeMillis();
    }

    private void ensureInitTimeExist() {
        Set<String> names = connMap.keySet();
        long initTime = System.currentTimeMillis();
        int index = 0;
        for (String name : names) {
            BIConnection connection = connMap.get(name);
            if (ComparatorUtils.equals(connection.getInitTime(), 0)) {
                connection.setInitTime(initTime + index);
                index++;
            }
        }
        if (index > 0) {
            try {
                FRContext.getCurrentEnv().writeResource(this);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public BIConnection getBIConnection(String name) {
        ensureInitTimeExist();
        return connMap.get(name);
    }

    @Override
    public Connection getConnection(String name) {
        return DatasourceManager.getProviderInstance().getConnection(name);
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                BIConnectionManager.getBIConnectionManager().envChanged();
            }
        });
    }

    @Override
    public void envChanged() {
        readXMLFile();
    }

    @Override
    public String fileName() {
        return "biconnection.xml";
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode() && ComparatorUtils.equals(reader.getTagName(), "conn")) {
            BIConnection connection = new BIConnection(reader.getAttrAsString("name", StringUtils.EMPTY),
                    reader.getAttrAsString("schema", null),
                    reader.getAttrAsLong("createBy", UserControl.getInstance().getSuperManagerID()),
                    reader.getAttrAsLong("initTime", 0));
            connMap.put(connection.getName(), connection);
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
            writer.attr("createBy", connection.getCreateBy());
            writer.attr("initTime", connection.getInitTime());
            writer.end();
        }
        writer.end();
    }


    @Override
    public void updateConnection(String linkData, String oldName, long userId) throws Exception {
        JSONObject linkDataJo = new JSONObject(linkData);
        String newName = linkDataJo.optString("name");
        DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
        if (!ComparatorUtils.equals(oldName, newName)) {
            datasourceManager.renameConnection(oldName, newName);
        }
        DataLinkInformation dl = new DataLinkInformation();
        dl.parseJSON(linkDataJo);

        Connection databaseConnection = dl.createDatabaseConnection();
        datasourceManager.putConnection(newName, databaseConnection);

        long createBy = getCreateBy(oldName, userId);
        long initTime = getInitTime(oldName);
        connMap.remove(oldName);
        connMap.put(newName, new BIConnection(newName, linkDataJo.optString("schema", null), createBy, initTime));
        try {
            FRContext.getCurrentEnv().writeResource(datasourceManager);
            FRContext.getCurrentEnv().writeResource(this);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void removeConnection(String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        connMap.remove(name);
        DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
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
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    private boolean isConnectionAvailable() {
        try {
//            c.isConnectionAvailable();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public JSONObject createJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
        Iterator<String> nameIt = datasourceManager.getConnectionNameIterator();

        int index = 0;
        while (nameIt.hasNext()) {
            String name = nameIt.next();
            Connection c = datasourceManager.getConnection(name);
            try {
                JSONObject jo = c.toJSONObject();
                jo.put("name", name);

                jo.put("createBy", getCreateBy(name, UserControl.getInstance().getSuperManagerID()));
                jo.put("initTime", getInitTime(name));
                if (isMicrosoftAccessDatabase(jo.optString("driver"), jo.optString("url"))) {
                    continue;
                }
                if (c.hasSchema()) {
                    jo.put("schema", getSchema(name));
                }
                jsonObject.put("link" + index++, jo);
            } catch (UnsupportedOperationException e) {
                logger.warn("the connection: " + c.toString() + " does not implement the toJSONObject method");
            }
        }

        return jsonObject;
    }

    @Override
    public boolean isMicrosoftAccessDatabase(String driver, String url) {
        return ComparatorUtils.equals("sun.jdbc.odbc.JdbcOdbcDriver", driver) && url.indexOf("Microsoft Access Driver") > 0;
    }

    @Override
    public boolean idNeedSchema(Connection c) {
        java.sql.Connection conn = null;
        if (isConnectionAvailable()) {
            try {
                conn = c.createConnection();
                Dialect dialect = DialectFactory.generateDialect(conn, c.getDriver());
                return dialect instanceof OracleDialect || dialect instanceof MSSQLDialect;
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            } finally {
                DBUtils.closeConnection(conn);
            }
        }
        return false;
    }
}
