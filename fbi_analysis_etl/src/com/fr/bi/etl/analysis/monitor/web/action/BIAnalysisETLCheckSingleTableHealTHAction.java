package com.fr.bi.etl.analysis.monitor.web.action;

import com.fr.bi.etl.analysis.monitor.MonitorUtils;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.service.action.AbstractAnalysisETLAction;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kary on 17-1-6.
 */
public class BIAnalysisETLCheckSingleTableHealTHAction extends AbstractAnalysisETLAction {
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        PrivilegeVote vote = getFSVote(req, res);
        FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
        if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
            BIServiceUtil.setPreviousUrl(req);
            vote.action(req, res);
            return;
        }
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        final long userId = ServiceUtils.getCurrentUserID(req);
        WebUtils.printAsJSON(res, MonitorUtils.calculateTablePosition(id, userId));
    }


    public String getCMD() {
        return "check_single_table_health";
    }

    private PrivilegeVote getFSVote(HttpServletRequest req, HttpServletResponse res) throws Exception {
        FSAuthentication authen = FSAuthenticationManager.exAuth4FineServer(req);
        if (authen == null) {
            //b:to improve
            AbstractFSAuthService.dealCookie(req, res);
            authen = FSAuthenticationManager.exAuth4FineServer(req);
        }
        return FSManager.getFSKeeper().access(authen);
    }

}
