package com.fr.bi.web.conf.services;

import com.finebi.cube.api.BICubeManager;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetFieldsInTableAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_fields_in_table";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String id = WebUtils.getHTTPRequestParameter(req, "id");

        if (StringUtils.isNotEmpty(id)) {
            BIBusinessTable table = new BIBusinessTable(id, userId);
            try {
                WebUtils.printAsJSON(res, table.createJSONWithFieldsInfo(BICubeManager.getInstance().fetchCubeLoader(userId)));
            } catch (NullPointerException e) {
                String tableString = WebUtils.getHTTPRequestParameter(req, "lastTable");
                WebUtils.printAsJSON(res, createTableJson(new JSONObject(tableString)));
            }

        } else {
            String tableString = WebUtils.getHTTPRequestParameter(req, "lastTable");
            WebUtils.printAsJSON(res, createTableJson(new JSONObject(tableString)));
        }
    }

    private JSONObject createTableJson(JSONObject jo) throws Exception {
        if (jo == null) {
            return new JSONObject();
        }
        String table_name = jo.optString("table_name", StringUtils.EMPTY);
        String db_name = jo.optString("connection_name", StringUtils.EMPTY);

        return new DBTableSource(db_name, table_name).createJSON();
    }
}