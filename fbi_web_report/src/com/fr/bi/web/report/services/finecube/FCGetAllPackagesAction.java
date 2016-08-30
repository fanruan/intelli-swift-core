package com.fr.bi.web.report.services.finecube;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.SessionIDInfor;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/8/29.
 */
public class FCGetAllPackagesAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "fc_get_all_packages";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        SessionIDInfor sessionInfo = SessionDealWith.getSessionIDInfor(sessionID);
        JSONObject result = new JSONObject();
        if (sessionInfo == null || !(sessionInfo instanceof BISession)) {
            result.put("error", "no session!");
        } else {
            BISession session = (BISession) sessionInfo;
            result = session.getAllAvailablePackagesGroups(req);
        }

        WebUtils.printAsJSON(res, result);
    }
}
