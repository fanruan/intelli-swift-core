package com.fr.bi.web.dezi.services;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetAccessablePackagesAction extends AbstractBIDeziAction {

    @Override
    public String getCMD() {
        return "get_accessable_packages";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        BISession session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        long userId = session.getUserIdFromSession(req);
        WebUtils.printAsJSON(res, BICubeConfigureCenter.getPackageManager().createPackageJSON(userId));
    }

}