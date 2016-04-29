package com.fr.bi.fe.service;

import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.WebActionsDispatcher;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 15-1-19.
 */
public class Service4Excel extends AbstractFSAuthService {
    /**
     * 注释
     *
     * @return 注释
     */
    @Override
    public String actionOP() {
        return "fr_excel";
    }

    private static ActionNoSessionCMD[] actions = {
            new BIExcelInitSharedTepAction(),
            new BIGetSharedUrlAction(),
            new BIExcelGetShareReportAction(),
            new BIExcelSetShareReportStatusAction(),
            new BIFEInviteAction(),
            new BIFELoginAction(),
            new BIFEUpdateUserInfoAction(),
            new BIFEOpenNewTab2ConfAction(),
            new BIInitFeDeziAction()
    };

    protected void init(HttpServletRequest req, HttpServletResponse res, String op) throws Exception {
    }

    /**
     * op=fs内的执行action
     *
     * @param req 请求
     * @param res 返回
     * @param op  当前操作类型
     * @throws Exception
     */
    @Override
    public void process(HttpServletRequest req, HttpServletResponse res, String op) throws Exception {
        if(WebUtils.getHTTPRequestParameter(req, "cmd") != null) {
            WebActionsDispatcher.dealForActionNoSessionIDCMD(req, res, actions);
            return;
        }
        init(req, res, op);
    }
}