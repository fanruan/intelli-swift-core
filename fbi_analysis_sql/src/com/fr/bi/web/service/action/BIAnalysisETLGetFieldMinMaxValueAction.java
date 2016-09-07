package com.fr.bi.web.service.action;

import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.sql.analysis.data.AnalysisSQLSourceFactory;
import com.fr.bi.sql.analysis.data.AnalysisSQLTableSource;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/23.
 */
public class BIAnalysisETLGetFieldMinMaxValueAction extends AbstractAnalysisSQLAction {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        String field = WebUtils.getHTTPRequestParameter(req, "field");
        if(StringUtils.isEmpty(field)){
            WebUtils.printAsJSON(res, new JSONObject());
            return;
        }
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableJSON = WebUtils.getHTTPRequestParameter(req, "table");
        JSONObject jo = new JSONObject(tableJSON);
        JSONArray items = jo.getJSONArray(Constants.ITEMS);
        AnalysisSQLTableSource source = AnalysisSQLSourceFactory.createTableSource(items, userId);
        JSONObject json = JSONObject.create();
//        json.put(BIJSONConstant.JSON_KEYS.FIELD_MIN_VALUE, tSet.first());
//        json.put(BIJSONConstant.JSON_KEYS.FILED_MAX_VALUE, tSet.last());
        WebUtils.printAsJSON(res, json);
    }


    @Override
    public String getCMD() {
        return "get_field_min_max_value";
    }
}
