package com.fr.bi.etl.analysis.monitor.web.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.etl.analysis.monitor.BITablePosition;
import com.fr.bi.etl.analysis.monitor.MonitorUtils;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.service.action.AbstractAnalysisETLAction;
import com.fr.bi.web.service.utils.BIAnalysisTableHelper;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kary on 17-1-6.
 */
public class BIAnalysisETLCheckAllTableHealTHAction extends AbstractAnalysisETLAction {
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        PrivilegeVote vote = getFSVote(req, res);
        FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
        if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
            BIServiceUtil.setPreviousUrl(req);
            vote.action(req, res);
            return;
        }
        final long userId = ServiceUtils.getCurrentUserID(req);
        WebUtils.printAsJSON(res, MonitorUtils.calculateTablePosition(userId));
    }


    public String getCMD() {
        return "check_all_table_health";
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
