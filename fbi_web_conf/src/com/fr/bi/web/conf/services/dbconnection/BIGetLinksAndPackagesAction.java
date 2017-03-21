package com.fr.bi.web.conf.services.dbconnection;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Young's on 2017/3/17.
 */
public class BIGetLinksAndPackagesAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONArray links = new JSONArray();
        DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
        Iterator<String> names = datasourceManager.getConnectionNameIterator();
        while (names.hasNext()) {
            String name = names.next();
            Connection conn = datasourceManager.getConnection(name);
            if (conn != null) {
                if (conn instanceof JDBCDatabaseConnection && BIConnectionManager.getInstance().isMicrosoftAccessDatabase(conn.getDriver(), ((JDBCDatabaseConnection) conn).getURL())) {
                    continue;
                }
                links.put(name);
            }
        }
        links.put(DBConstant.CONNECTION.SERVER_CONNECTION);

        List<BIPackageID> packageIDs = BIConfigureManagerCenter.getAuthorityManager().getAuthPackagesByUser(userId);
        JSONObject packages = new JSONObject();
        for (BIPackageID packageID : packageIDs) {
            BIBusinessPackage biBusinessPackage = (BIBusinessPackage) BICubeConfigureCenter.getPackageManager().getPackage(userId, packageID);
            packages.put(biBusinessPackage.getID().getIdentityValue(), biBusinessPackage.createJSON());
        }

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
