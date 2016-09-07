package com.fr.bi.web.service.action;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.sql.analysis.data.AnalysisSQLSourceFactory;
import com.fr.bi.sql.analysis.data.AnalysisSQLTableSource;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by 小灰灰 on 2016/4/19.
 */
public class BIAnalysisETLGetFieldValueAction extends AbstractAnalysisSQLAction {
    private static final int MAX_ROW = 1000;

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
        JSONArray ja = new JSONArray();
        BIKey key = new IndexKey(field);

        JSONObject result = JSONObject.create();
        result.put(BIJSONConstant.JSON_KEYS.VALUE, ja);
        WebUtils.printAsJSON(res, result);
    }

    private Object getText(Object ob, int fieldType) {
        if (fieldType == DBConstant.COLUMN.DATE){
            return DateUtils.format(new Date((Long)ob));
        }
        return ob;
    }

    @Override
    public String getCMD() {
        return "get_field_value";
    }
}
