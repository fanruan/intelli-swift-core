package com.fr.bi.web.base;


import com.fr.bi.web.base.fs.BIFSGetConfigAction;
import com.fr.bi.web.base.fs.BIFSSetConfigAction;
import com.fr.bi.web.base.services.BICheckValidationOfExpressionAction;
import com.fr.bi.web.base.services.BIGetPyAction;
import com.fr.bi.web.base.services.BIGetTableAction;
import com.fr.bi.web.base.services.BIGetTransNameAction;
import com.fr.fs.FSContext;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.stable.fun.impl.NoSessionIDService;
import com.fr.web.core.WebActionsDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BI数据源配置
 *
 * @author Daniel-pc
 */
public class Service4BIBase extends NoSessionIDService {

    private static AbstractBIBaseAction[] actions = {
            new BIGetPyAction(),
            new BIGetTableAction(),
            new BIGetTransNameAction(),
            new BIFSGetConfigAction(),
            new BIFSSetConfigAction(),
            new BICheckValidationOfExpressionAction()

    };

    /**
     * 返回
     *
     * @return 名称
     */
    @Override
    public String actionOP() {
        return "fr_bi_base";
    }

    /**
     * 处理HTTP请求
     *
     * @param req HTTP请求
     * @param res HTTP响应
     * @param op  op参数值
     * @throws Exception
     */
    @Override
    public void process(HttpServletRequest req, HttpServletResponse res,
                        String op) throws Exception {
        FSContext.initData();
        res.setHeader("Pragma", "No-cache");
        res.setHeader("Cache-Control", "no-cache, no-store");
        res.setDateHeader("Expires", -10);

        PrivilegeVote vote = getFSVote(req, res);
        FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
        if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
            vote.action(req, res);
            return;
        }
//        long userId = ServiceUtils.getCurrentUserID(req);
//        if (UserControl.getInstance().hasModulePrivilege(userId, FSConstants.MODULEID.BI)) {
            WebActionsDispatcher.dealForActionNoSessionIDCMD(req, res, actions);
//        }
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