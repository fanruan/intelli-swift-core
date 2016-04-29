package com.fr.bi.fe.service.noneedlogin;

import com.fr.base.FRContext;
import com.fr.bi.conf.aconfig.BIInterfaceAdapter;
import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONException;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Level;

/**
 * Created by young on 2014/12/16.
 */
public class FERemoveTableFromBusiPackage extends AbstractBIConfigureAction {

    @Override
	public String getCMD() {
        return "fe_remove_table_from_business_package";
    }

    @Override
	protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String connectionName = WebUtils.getHTTPRequestParameter(req, "connection_name");
        String md5tableName = WebUtils.getHTTPRequestParameter(req, "md5_table_name");
        String packageName = "Excel数据集";
        String schemaName = WebUtils.getHTTPRequestParameter(req, "schema_name");

        doRemoveTableFromBusinessPackage(req, packageName, connectionName, md5tableName, schemaName);
    }

    private void doRemoveTableFromBusinessPackage(HttpServletRequest req, String packageName, String connectionName, String md5tableName,String schemaName) throws JSONException {
        long userId = ServiceUtils.getCurrentUserID(req);
        BIInterfaceAdapter.getBIBusiPackAdapter().removeTableFromBusiPackByTableName(packageName, connectionName, md5tableName, schemaName, userId);
        try {
            FRContext.getCurrentEnv().writeResource(BIInterfaceAdapter.getBIBusiPackAdapter().getBusiPackageManager(userId));
        } catch (Exception e) {
            FRContext.getLogger().log(Level.WARNING, e.getMessage(), e);
        }
    }
}