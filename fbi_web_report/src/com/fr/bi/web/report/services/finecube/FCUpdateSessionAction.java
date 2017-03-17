package com.fr.bi.web.report.services.finecube;

import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/8/29.
 */
public class FCUpdateSessionAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "fc_update_session";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        SessionDealWith.updateSessionID(sessionID);
    }
}
