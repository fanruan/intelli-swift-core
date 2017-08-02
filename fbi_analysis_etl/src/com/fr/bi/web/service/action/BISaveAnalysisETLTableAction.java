package com.fr.bi.web.service.action;

import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class BISaveAnalysisETLTableAction extends AbstractAnalysisETLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = BIUserAuthUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String newId = WebUtils.getHTTPRequestParameter(req, "new_id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        String describe = WebUtils.getHTTPRequestParameter(req, "describe");
        String tableJSON = WebUtils.getHTTPRequestParameter(req, "table");
        JSONObject result = BIAnalysisETLManagerCenter.getBusiPackManager().saveAnalysisETLTable(userId,tableId,newId,tableName,describe,tableJSON);
        WebUtils.printAsJSON(res, result);
    }

    @Override
    public String getCMD() {
        return "save_table";
    }


}
