package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIDesignReport;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.report.utils.BIFSReportUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2015/7/16.
 */
public class BIAddReportAction extends ActionNoSessionCMD {

    private final static String ACTION_CMD = "add_report";

    @Override
    public String getCMD() {
        return ACTION_CMD;
    }

    /**
     * 执行方法
     *
     * @param req http请求
     * @param res http应答
     * @throws Exception
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        String reportName = WebUtils.getHTTPRequestParameter(req, "reportName");
        String reportLocation = WebUtils.getHTTPRequestParameter(req, "reportLocation");
        String realTime = WebUtils.getHTTPRequestParameter(req, "realTime");
        String popConfig = WebUtils.getHTTPRequestParameter(req, "popConfig");
        long userId = ServiceUtils.getCurrentUserID(req);
        //构造一个空的report
        if(popConfig == null) {
            JSONObject reportJO = new JSONObject();
            reportJO.put("widgets", new JSONObject());
            reportJO.put("layoutType", 0);
            popConfig = reportJO.toString();
        }
        BIDesignReport report = new BIDesignReport(new BIDesignSetting(popConfig));
        long reportId = BIFSReportUtils.createNewBIReport(report, userId, reportName, realTime == null ? "" : realTime);

        //保存到文件夹
        BIReportNode reportNode = BIDAOUtils.findByID(reportId, userId);
        reportNode.setParentid(reportLocation);
        BIDAOUtils.saveOrUpDate(reportNode, userId);

        JSONObject jo = new JSONObject();
        jo.put(BIBaseConstant.REPORT_ID, reportId);
        WebUtils.printAsJSON(res, jo);
    }
}