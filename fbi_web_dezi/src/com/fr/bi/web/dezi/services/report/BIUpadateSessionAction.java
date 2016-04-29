package com.fr.bi.web.dezi.services.report;

import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.web.core.SessionDealWith;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIUpadateSessionAction extends AbstractBIDeziAction {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        SessionDealWith.updateSessionID(sessionID);
    }

    @Override
    public String getCMD() {
        return "update_session";
    }

}