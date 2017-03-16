package com.fr.bi.web.report.services.finecube;

import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/8/29.
 */
public class FCCheckSessionAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "fc_check_session";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        WebUtils.printAsJSON(res, new JSONObject().put("check", SessionDealWith.hasSessionID(sessionID)));
    }
}
