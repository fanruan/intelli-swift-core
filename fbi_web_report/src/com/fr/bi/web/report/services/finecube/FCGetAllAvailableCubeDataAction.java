package com.fr.bi.web.report.services.finecube;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.util.BIConfUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.SessionIDInfor;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Young's on 2016/9/1.
 */
public class FCGetAllAvailableCubeDataAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "fc_get_all_available_cube_data";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        SessionIDInfor sessionInfo = SessionDealWith.getSessionIDInfor(sessionID);
        JSONObject result = new JSONObject();
        if (sessionInfo == null || !(sessionInfo instanceof BISession)) {
            result.put("error", "no session!");
        } else {
            Map<String, JSONObject> map = BIConfUtils.getAvailableData(req);
            result.put("source", map.get("source"));
            result.put("groups", map.get("groups"));
            result.put("packages", map.get("packages"));
            result.put("relations", map.get("relations"));
            result.put("connections", map.get("connections"));
            result.put("translations", map.get("translations"));
            result.put("tables", map.get("tables"));
            result.put("fields", map.get("fields"));
            result.put("noAuthFields", map.get("noAuthFields"));
            result.put("excel_views", map.get("excelViews"));
        }
        WebUtils.printAsJSON(res, result);
    }
}
