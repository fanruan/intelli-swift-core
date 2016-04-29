package com.fr.bi.web.dezi.web;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.bi.web.dezi.BIReportSavingAction;
import com.fr.bi.web.dezi.BIWidgetSettingAction;
import com.fr.bi.web.dezi.services.*;
import com.fr.bi.web.dezi.services.report.BIUpadateSessionAction;
import com.fr.bi.web.report.services.BIInitDeziPaneAction;
import com.fr.fs.FSContext;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.stable.fun.Service;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.WebActionsDispatcher;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

public class Service4BIDezi implements Service {
    /*
    登陆后需要跳转回来的action
     */
    private static Set<String> redirectSet = new HashSet<String>();

    static {
        redirectSet.add(BIInitDeziPaneAction.CMD);
        redirectSet.add(BIExcelExportAction.CMD);
        redirectSet.add(BIWidgetSettingAction.CMD);
    }

    private static AbstractBIDeziAction[] actions = {
            new BIUpadateSessionAction(),
            new BIRemoveWidgetAction(),

            new BIWidgetSettingAction(),
            new BIReportSavingAction(),

            new BIGetAccessablePackagesAction(),
            new BIGetAccessableGroupPackagesAction(),

            new BIExcelExportAction(),

            new BIGetPreviewTableDataAction()

    };

    /**
     * 返回该服务所附带的OP参数
     *
     * @return op参数
     */
    @Override
    public String actionOP() {
        return "fr_bi_dezi";
    }

    /**
     * 处理HTTP请求
     *
     * @param req       HTTP请求
     * @param res       HTTP响应
     * @param op        op参数值
     * @param sessionID 当前广义报表对象的会话ID
     * @throws Exception
     */
    @Override
    public void process(HttpServletRequest req, HttpServletResponse res,
                        String op, String sessionID) throws Exception {
        FSContext.initData();
        res.setHeader("Pragma", "No-cache");
        res.setHeader("Cache-Control", "no-cache, no-store");
        res.setDateHeader("Expires", -10);
        dealServletPriviousUrl(req);
        PrivilegeVote vote = getFSVote(req, res);
        FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
        BISession biSessionInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);

        if ((biSessionInfor != null)) {
            WebActionsDispatcher.dealForActionCMD(req, res, sessionID, actions);
        } else if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
            vote.action(req, res);
        } else {
            ErrorHandlerHelper.getErrorHandler().error(req, res, "Reportlet SessionId: \"" + sessionID + "\"time out. ");
        }
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

    private void dealServletPriviousUrl(HttpServletRequest req) {
        String cmd = WebUtils.getHTTPRequestParameter(req, "cmd");
        if (redirectSet.contains(cmd)) {
            BIServiceUtil.setPreviousUrl(req);
        }
    }
}