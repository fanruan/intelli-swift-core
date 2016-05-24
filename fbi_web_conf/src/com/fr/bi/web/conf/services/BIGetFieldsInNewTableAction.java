package com.fr.bi.web.conf.services;


import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 14-8-19.
 */
public class BIGetFieldsInNewTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String tableString = WebUtils.getHTTPRequestParameter(req, BIJSONConstant.JSON_KEYS.TABLE);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        long userId = ServiceUtils.getCurrentUserID(req);
        CubeTableSource source = TableSourceFactory.createTableSource(new JSONObject(tableString), userId);
//
//        BIBusinessTable table = new BIBusinessTable(tableId, userId);
//        table.setSource(source);
//        WebUtils.printAsJSON(res, table.createJSON());
    }

    @Override
    public String getCMD() {
        return "get_fields_new_table";
    }
}