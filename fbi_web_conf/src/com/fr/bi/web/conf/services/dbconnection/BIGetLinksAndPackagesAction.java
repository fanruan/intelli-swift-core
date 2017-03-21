package com.fr.bi.web.conf.services.dbconnection;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * Created by Young's on 2017/3/17.
 */
public class BIGetLinksAndPackagesAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONArray links = new JSONArray();
        DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
        Iterator<String> names = datasourceManager.getConnectionNameIterator();
        while (names.hasNext()) {
            String name = names.next();
            Connection conn = datasourceManager.getConnection(name);
            if (conn != null) {
                if (conn instanceof JDBCDatabaseConnection && ComparatorUtils.equals(conn.getDriver(), "sun.jdbc.odbc.JdbcOdbcDriver") && ((JDBCDatabaseConnection) conn).getURL().indexOf("Microsoft Access Driver") > 0) {
                    continue;
                }
                links.put(name);
            }
        }
        links.put(DBConstant.CONNECTION.SERVER_CONNECTION);

        JSONObject packages = BICubeConfigureCenter.getPackageManager().createPackageJSON(UserControl.getInstance().getSuperManagerID());

        JSONObject jo = new JSONObject();
        jo.put("links", links);
        jo.put("packages", packages);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_links_packages";
    }
}
