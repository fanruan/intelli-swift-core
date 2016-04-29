package com.fr.bi.web.dezi.mobile;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.fs.FSContext;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.stable.fun.Service;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.WebActionsDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2015/9/6.
 */
public class Service4BIMobile implements Service {


    private static AbstractBIDeziAction[] actions = {
    };

    /**
     * 返回该服务所附带的OP参数
     *
     * @return op参数
     */
    @Override
    public String actionOP() {
        return "fr_bi_moble_dezi";
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
        PrivilegeVote vote = getFSVote(req, res);
        FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);
        BISession biSessionInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);

        if ((biSessionInfor != null && biSessionInfor.isSharedReq())) {
            WebActionsDispatcher.dealForActionCMD(req, res, sessionID, actions);
        } else if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
            vote.action(req, res);
        } else {
            WebActionsDispatcher.dealForActionCMD(req, res, sessionID, actions);
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