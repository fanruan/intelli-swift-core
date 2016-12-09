package com.fr.bi.web.dezi.services.report;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.SessionIDInfor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIUpdateSessionAction extends AbstractBIDeziAction {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        SessionDealWith.updateSessionID(sessionID);
//        更新模板的锁的时间
        if (ClusterEnv.isCluster()) {
            SessionIDInfor ss = SessionDealWith.getSessionIDInfor(sessionID);
            ((BISession) ss).updateReportNodeLockTime();
//            ((BISession) ss).updateConfigLockTime();
        }
    }

    @Override
    public String getCMD() {
        return "update_session";
    }

}