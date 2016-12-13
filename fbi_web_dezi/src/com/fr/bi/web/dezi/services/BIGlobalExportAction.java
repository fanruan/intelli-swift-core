package com.fr.bi.web.dezi.services;

import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * 模板全局导出
 * Created by AstronautOO7 on 2016/12/13.
 */
public class BIGlobalExportAction extends AbstractBIDeziAction {

    public static final String CMD = "bi_global_export";


    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        BISession sessionIDInfo = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (sessionIDInfo == null) {
            ErrorHandlerHelper.getErrorHandler().error(req, res, "Reportlet SessionID: \"" + sessionID + "\"time out.");
            return;
        }

        String reportId = WebUtils.getHTTPRequestParameter(req, "reportId");
        BIReportNode node = BIDAOUtils.findByID(Long.parseLong(reportId), ServiceUtils.getCurrentUserID(req));
        String reportName = node.getReportName() != null ? node.getReportName() : "";

        if (StringUtils.isEmpty(reportName)) {
            return;
        }

        JSONObject reportSetting = BIReadReportUtils.getBIReportNodeJSON(node);
        JSONObject widgets = (JSONObject) reportSetting.get("widgets");

        Iterator it = widgets.keys();
        while (it.hasNext()) {
            String wId = (String) it.next();
            JSONObject widget = widgets.getJSONObject(wId);
            String widgetName = BIWidgetFactory.parseWidget(widget, node.getUserId()).getWidgetName();
        }

        long t = System.currentTimeMillis();
    }
}
