package com.fr.bi.fe.service;

import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.web.services.util.BIWebUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.stable.web.Weblet;
import com.fr.web.core.ActionNoSessionCMD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2014/12/16.
 */
public class BIInitFeDeziAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "init_fe_dezi";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        dealWidthWeblet(req, res, new BIWeblet());
    }

    /**
     * 注释
     * @param req 注释
     * @param res 注释
     * @param let 注释
     * @return 注释
     */
    public static void dealWidthWeblet(HttpServletRequest req, HttpServletResponse res, Weblet let) throws Exception{
        BIWebUtils.dealWidthWeblet(req, res, let, null, ServiceUtils.getCurrentUserID(req), null, "fine_excel", null);
    }
}