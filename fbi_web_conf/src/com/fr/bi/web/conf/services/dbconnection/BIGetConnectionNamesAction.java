package com.fr.bi.web.conf.services.dbconnection;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * Created by Young's on 2015/8/13.
 */
public class BIGetConnectionNamesAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONArray ja = new JSONArray();
        DatasourceManagerProvider datasourceManager = DatasourceManager.getInstance();
        Iterator names = datasourceManager.getConnectionNameIterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            JDBCDatabaseConnection conn = datasourceManager.getConnection(name, JDBCDatabaseConnection.class);
            if (conn != null) {
                if (ComparatorUtils.equals(conn.getDriver(), "sun.jdbc.odbc.JdbcOdbcDriver") && conn.getURL().indexOf("Microsoft Access Driver") > 0) {
                    continue;
                }
                ja.put(name);
            }
        }

        boolean discardServer = WebUtils.getHTTPRequestBoolParameter(req, "discard_server");
        if (!discardServer) {
            ja.put(DBConstant.CONNECTION.SERVER_CONNECTION);
        }

        WebUtils.printAsJSON(res, ja);
    }

    @Override
    public String getCMD() {
        return "get_connection_names";
    }
}