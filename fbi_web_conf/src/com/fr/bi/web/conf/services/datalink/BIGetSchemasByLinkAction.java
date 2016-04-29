package com.fr.bi.web.conf.services.datalink;

import com.fr.base.FRContext;
import com.fr.bi.stable.data.db.DataLinkInformation;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.web.base.JSONErrorHandler;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.data.core.DataCoreUtils;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/3/25.
 */
public class BIGetSchemasByLinkAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String sLinkData = WebUtils.getHTTPRequestParameter(req, "linkData");
        com.fr.data.impl.Connection dbc = fetchConnection(sLinkData);
        boolean isOracle = false;
        JSONArray ja = new JSONArray();
        try {
            isOracle = FRContext.getCurrentEnv().isOracle(dbc);
        } catch (Exception e){
            new JSONErrorHandler().error(req, res, e.getMessage());
        }
        if(isOracle){
            String[] schemas = DataCoreUtils.getDatabaseSchema(dbc);
            for(int i = 0; i < schemas.length; i++){
                ja.put(schemas[i]);
            }
        }
        WebUtils.printAsJSON(res, ja);
    }

    private JDBCDatabaseConnection fetchConnection(String configData) throws Exception {
        JSONObject linkDataJo = new JSONObject(configData);
        DataLinkInformation dl = new DataLinkInformation();
        dl.parseJSON(linkDataJo);
        JDBCDatabaseConnection c = dl.createJDBCDatabaseConnection();
        BIDBUtils.dealWithJDBCConnection(c);
        return c;
    }

    @Override
    public String getCMD() {
        return "get_schemas_by_link";
    }
}
