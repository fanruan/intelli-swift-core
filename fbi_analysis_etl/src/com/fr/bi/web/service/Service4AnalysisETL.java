package com.fr.bi.web.service;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.web.service.action.*;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.stable.fun.Service;
import com.fr.stable.web.RequestCMDReceiver;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.WebActionsDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class Service4AnalysisETL implements Service {


    private RequestCMDReceiver[] actions = {
            new BISaveAnalysisETLTableAction(),
            new BIAnalysisETLGetFieldValueAction(),
            new BIDeleteAnalysisETLTableAction(),
            new BIEditAnalysisETLTableAction(),
            new BIRenameAnalysisETLTableAction(),
            new BIPreviewAnalysisETLTableAction(),
            new BIAnalysisETLGetFieldMinMaxValueAction(),
            new BIAnalysisETLGetGeneratingStatusAction(),
            new BIGetUsedTablesAction(),
            new BIAnalysisETLCheckAllTableStatusAction(),
            new BIAnalysisETLCheckTableInUseAction()
    };


    public String actionOP() {
        return "fr_bi_analysis_etl";
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse res, String op, String sessionID) throws Exception {
        BISession biSessionInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (biSessionInfor != null) {
            WebActionsDispatcher.dealForActionCMD(req, res, sessionID, actions);
        } else {
            FSAuthentication authentication = BIUserAuthUtils.getFSAuthentication(req);
            PrivilegeVote vote = FSManager.getFSKeeper().access(authentication);
            if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
                vote.action(req, res);
            } else {
                ErrorHandlerHelper.getErrorHandler().error(req, res, "Reportlet SessionId: \"" + sessionID + "\"time out. ");
            }
        }
    }

}
