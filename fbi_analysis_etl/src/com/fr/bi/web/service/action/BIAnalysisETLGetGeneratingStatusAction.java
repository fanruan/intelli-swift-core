package com.fr.bi.web.service.action;

import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.web.service.utils.BIAnalysisTableHelper;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/6/2.
 */
public class BIAnalysisETLGetGeneratingStatusAction extends AbstractAnalysisETLAction {

    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = BIUserAuthUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        double percent = BIAnalysisTableHelper.getTableGeneratingProcessById(tableId, userId);
        JSONObject jo = new JSONObject();
        jo.put(Constants.GENERATED_PERCENT, percent);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_cube_status";
    }
}

