package com.fr.bi.web.conf.services.session;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.web.core.SessionDealWith;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/12/14.
 */
public class BIConfUpdateSession extends AbstractBIConfigureAction {
    @Override
    public String getCMD() {
        return "update_session";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        SessionDealWith.updateSessionID(sessionID);
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

    }
}
