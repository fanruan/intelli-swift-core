package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIDesignReport;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.web.report.utils.BIFSReportManager;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/7/16.
 */
public class BIReportSaveAsAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "report_save_as";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String s) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String reportId = WebUtils.getHTTPRequestParameter(req, "report_id");
        String createBy = WebUtils.getHTTPRequestParameter(req, "create_by");
        String reportName = WebUtils.getHTTPRequestParameter(req, "report_name");
        String reportLocation = WebUtils.getHTTPRequestParameter(req, "report_location");
        String realTime = WebUtils.getHTTPRequestParameter(req, "real_time");
        String config = WebUtils.getHTTPRequestParameter(req, "config");

        reportLocation = reportLocation == null ? "-1" : reportLocation;
        JSONObject jo = new JSONObject();
        if (config != null) {
            JSONObject reportSetting = new JSONObject(config);
            BIDesignReport report = new BIDesignReport(new BIDesignSetting(reportSetting.toString()));
            long newId = BIFSReportManager.getBIFSReportManager().createNewBIReport(report, userId, reportName, reportLocation, realTime == null ? "" : realTime);
            jo.put(BIBaseConstant.REPORT_ID, newId);
        } else {
            BIReportNode node = BIDAOUtils.getBIDAOManager().findByID(Long.parseLong(reportId), Long.parseLong(createBy));
            if (node != null) {
                JSONObject reportSetting = BIReadReportUtils.getBIReadReportManager().getBIReportNodeJSON(node);
                BIDesignReport report = new BIDesignReport(new BIDesignSetting(reportSetting.toString()));
                long newId = BIFSReportManager.getBIFSReportManager().createNewBIReport(report, userId, reportName, reportLocation, realTime == null ? "" : realTime);
                jo.put(BIBaseConstant.REPORT_ID, newId);
            } else {
                jo.put("message", "can not find report: " + reportName);
            }
        }
        WebUtils.printAsJSON(res, jo);
    }
}
