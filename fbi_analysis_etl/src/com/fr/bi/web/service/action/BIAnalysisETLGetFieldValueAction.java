package com.fr.bi.web.service.action;

import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/4/19.
 */
public class BIAnalysisETLGetFieldValueAction extends AbstractAnalysisETLAction{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        String field = WebUtils.getHTTPRequestParameter(req, "field");
        if(StringUtils.isEmpty(field)){
            WebUtils.printAsJSON(res, new JSONObject());
            return;
        }
        long userId = ServiceUtils.getCurrentUserID(req);
//        ITableSource source = AnalysisETLSourceFactory.createTableSource(new JSONObject(WebUtils.getHTTPRequestParameter(req, "table")), userId);
//        Set set = source.getFieldDistinctNewestValues(field, CubeReadingTableIndexLoader.getInstance(userId), userId);

        JSONArray ja = new JSONArray();
        ja.put("1");
        ja.put("2");
        ja.put("3");
        ja.put("4");
        ja.put("5");
        ja.put("6");

        JSONObject jo = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.VALUE, ja);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_field_value";
    }
}
