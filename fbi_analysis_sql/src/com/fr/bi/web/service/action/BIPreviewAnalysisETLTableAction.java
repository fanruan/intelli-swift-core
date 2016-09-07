package com.fr.bi.web.service.action;

import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.sql.analysis.data.AnalysisSQLSourceFactory;
import com.fr.bi.sql.analysis.data.AnalysisSQLTableSource;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/16.
 */
public class BIPreviewAnalysisETLTableAction extends AbstractAnalysisSQLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String itemArray = WebUtils.getHTTPRequestParameter(req, Constants.ITEMS);
        JSONArray items = new JSONArray(itemArray);
        AnalysisSQLTableSource source = AnalysisSQLSourceFactory.createTableSource(items, userId);
        JSONObject result = JSONObject.create();
        result.put(BIJSONConstant.JSON_KEYS.VALUE, source.toSql());
        WebUtils.printAsJSON(res, result);
    }

    @Override
    public String getCMD() {
        return "preview_table";
    }
}
