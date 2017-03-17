package com.fr.bi.web.dezi;

import com.fr.bi.fs.BIDesignReport;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.base.operation.BIOperationStore;
import com.fr.bi.web.report.utils.BIFSReportManager;
import com.fr.bi.web.report.utils.BIFSReportUtils;
import com.fr.json.JSONArray;
import com.fr.stable.StableUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2015/9/9.
 */
public class BIReportSavingAction extends AbstractBIDeziAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String s) throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, BIBaseConstant.REPORT_ID);
        String reportName = WebUtils.getHTTPRequestParameter(req, "reportName");
        String popConfig = WebUtils.getHTTPRequestParameter(req, "popConfig");
        String createBy = WebUtils.getHTTPRequestParameter(req, "createBy");

        long reportId = StableUtils.string2Number(id).longValue();
        long userId = StableUtils.string2Number(createBy).longValue();
        BIFSReportManager.getBIFSReportManager().updateExistBIReport(new BIDesignReport(new BIDesignSetting(popConfig)), userId, s);
        BIOperationStore store = new BIOperationStore(userId, reportId, reportName, popConfig, new JSONArray());
        store.startRecording();
    }

    @Override
    public String getCMD() {
        return "report_saving";
    }
}