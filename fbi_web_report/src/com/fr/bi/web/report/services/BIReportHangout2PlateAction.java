package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.entry.BIReportEntry;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.EntryControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/6/3.
 */
public class BIReportHangout2PlateAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String report = WebUtils.getHTTPRequestParameter(req, "report");
        JSONObject reportJO = new JSONObject(report);
        BIReportEntry entry = new BIReportEntry();
        entry.parseJSON(reportJO);
        EntryControl.getInstance().checkSaveOrUpdateEntryPrivilege(entry, userId);
        EntryControl.getInstance().saveOrUpdateEntry(entry);
        JSONObject jo = entry.createJSONConfig();
        try {
            long createBy = reportJO.getLong("createBy");
            BIReportNode reportNode = BIDAOUtils.findByID(reportJO.getLong("reportId"), createBy);
            reportNode.setStatus(BIReportConstant.REPORT_STATUS.HANGOUT);
            BIDAOUtils.saveOrUpDate(reportNode, createBy);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "hangout_report_to_plate";
    }
}
