package com.fr.bi.web.service;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.web.service.action.*;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.stable.fun.Service;
import com.fr.web.core.ActionCMD;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.WebActionsDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class Service4AnalysisETL implements Service {

    private static ActionCMD[] actions = {
        new BISaveAnalysisETLTableAction(),
        new BIAnalysisETLGetFieldValueAction(),
        new BIDeleteAnalysisETLTableAction(),
        new BIEditAnalysisETLTableAction(),
        new BIRenameAnalysisETLTableAction()
    };

    @Override
    public String actionOP() {
        return "fr_bi_analysis_etl";
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse res, String op, String sessionID) throws Exception {
        BISession biSessionInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (biSessionInfor != null) {
            WebActionsDispatcher.dealForActionCMD(req, res, sessionID, actions);
        } else {
            PrivilegeVote vote = getFSVote(req, res);
            FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
            if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
                vote.action(req, res);
            }  else {
                ErrorHandlerHelper.getErrorHandler().error(req, res, "Reportlet SessionId: \"" + sessionID + "\"time out. ");
            }
        }
    }

    private PrivilegeVote getFSVote(HttpServletRequest req, HttpServletResponse res) throws Exception {
        FSAuthentication authen = FSAuthenticationManager.exAuth4FineServer(req);
        if (authen == null) {
            AbstractFSAuthService.dealCookie(req, res);
            authen = FSAuthenticationManager.exAuth4FineServer(req);
        }
        return FSManager.getFSKeeper().access(authen);
    }
}
