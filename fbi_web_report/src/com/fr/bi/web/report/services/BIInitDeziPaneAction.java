package com.fr.bi.web.report.services;

import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.cal.stable.utils.BIReportUtils;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIInitDeziPaneAction extends ActionNoSessionCMD {

    public static final String CMD = "init_dezi_pane";

    /**
     * 注释
     *
     * @param req 注释
     * @param res 注释
     * @return 注释
     */
    public static void dealWithWebPage(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String reportId = WebUtils.getHTTPRequestParameter(req, BIBaseConstant.REPORT_ID);
        BIReportNode node = BIDAOUtils.findByID(Long.parseLong(reportId), ServiceUtils.getCurrentUserID(req));
        JSONObject reportSetting = BIReportUtils.getBIReportNodeJSON(node);
        BIWebUtils.dealWithWebPage(req, res, new BIWeblet(node), reportSetting, node);
    }

    @Override
    public String getCMD() {
        return CMD;
    }

    /**
     * 注释
     *
     * @param req       注释
     * @param res       注释
     * @return 注释
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        BIServiceUtil.setPreviousUrl(req);
        dealWithWebPage(req, res);
    }
}