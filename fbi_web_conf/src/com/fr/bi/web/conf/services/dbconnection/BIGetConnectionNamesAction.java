package com.fr.bi.web.conf.services.dbconnection;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.base.datasource.BIConnection;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
<<<<<<< HEAD
import com.fr.bi.web.conf.utils.BIWebConfUtils;
=======
import com.fr.data.impl.Connection;
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Young's on 2015/8/13.
 */
public class BIGetConnectionNamesAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONArray ja = new JSONArray();
        DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
        Iterator names = datasourceManager.getConnectionNameIterator();
        boolean isAdmin = ComparatorUtils.equals(userId, UserControl.getInstance().getSuperManagerID());
        Set<BIDataConfigAuthority> authorities = BIConfigureManagerCenter.getDataConfigAuthorityManager().getDataConfigAuthoritiesByUserId(userId);
        List<String> authNames = new ArrayList<String>();
        boolean serverAuth = false;
        for (BIDataConfigAuthority authority : authorities) {
            if (ComparatorUtils.equals(authority.getpId(), DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER.DATA_CONNECTION)) {
                String connId = authority.getId().substring(authority.getpId().length());
                String connName = BIWebConfUtils.getConnectionNameByID(connId);
                authNames.add(connName);
            }
            if (ComparatorUtils.equals(authority.getId(), DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER.SERVER_CONNECTION)) {
                serverAuth = true;
            }
        }
        while (names.hasNext()) {
            String name = (String) names.next();
<<<<<<< HEAD
            BIConnection connection = BIConnectionManager.getInstance().getBIConnection(name);
            if (isAdmin || ComparatorUtils.equals(connection.getCreateBy(), userId) || authNames.contains(name)) {
                JDBCDatabaseConnection conn = datasourceManager.getConnection(name, JDBCDatabaseConnection.class);
                if (conn != null) {
                    if (ComparatorUtils.equals(conn.getDriver(), "sun.jdbc.odbc.JdbcOdbcDriver") && conn.getURL().indexOf("Microsoft Access Driver") > 0) {
                        continue;
                    }
                    ja.put(name);
=======

            Connection conn = datasourceManager.getConnection(name);
            if (conn != null) {
                if (conn instanceof JDBCDatabaseConnection && ComparatorUtils.equals(conn.getDriver(), "sun.jdbc.odbc.JdbcOdbcDriver") && ((JDBCDatabaseConnection) conn).getURL().indexOf("Microsoft Access Driver") > 0) {
                    continue;
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
                }
            }
        }
        boolean discardServer = WebUtils.getHTTPRequestBoolParameter(req, "discard_server");
        if (!discardServer && (isAdmin || serverAuth)) {
            ja.put(DBConstant.CONNECTION.SERVER_CONNECTION);
        }
        WebUtils.printAsJSON(res, ja);
    }

    @Override
    public String getCMD() {
        return "get_connection_names";
    }
}