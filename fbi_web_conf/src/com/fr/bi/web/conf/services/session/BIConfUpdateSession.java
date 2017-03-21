package com.fr.bi.web.conf.services.session;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/12/15.
 */
public class BIConfUpdateSession extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String sessionID = WebUtils.getHTTPRequestParameter(req, "sessionID");
        SessionDealWith.updateSessionID(sessionID);
    }

    @Override
    public String getCMD() {
        return "update_session";
    }
}
