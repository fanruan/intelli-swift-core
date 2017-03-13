package com.fr.bi.web.dezi;

import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.fs.BIDesignReport;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.base.operation.BIOperationStore;
import com.fr.bi.web.report.utils.BIFSReportManager;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

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


        updateSessionWidget(popConfig, userId, s);

        BIFSReportManager.getBIFSReportManager().updateExistBIReport(new BIDesignReport(new BIDesignSetting(popConfig)), userId, s);
        BIOperationStore store = new BIOperationStore(userId, reportId, reportName, popConfig, new JSONArray());
        store.startRecording();
    }

    private void updateSessionWidget(String report, long userId, String sessionId) throws Exception {
        BISession sessionIDInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionId);
        JSONObject reportJSON = new JSONObject(report);
        BIReport biReport = sessionIDInfor.getBIReport();

        if (reportJSON.has("widgets")) {
            JSONObject widgets = reportJSON.getJSONObject("widgets");
            Iterator<String> iterator = reportJSON.getJSONObject("widgets").toMap().keySet().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                JSONObject JSONWidget = widgets.optJSONObject(iterator.next());
                JSONObject viewJSON = new JSONObject();
                if (JSONWidget.has("view")) {
                    viewJSON = JSONWidget.optJSONObject("view");
                }
                JSONArray viewTargets = BIWidgetFactory.getViewTarget(viewJSON);
                BIWidget widget = BIWidgetFactory.newWidgetByType(WidgetType.parse(JSONWidget.optInt("type")), viewTargets);
                widget.parseJSON(JSONWidget, userId);
                biReport.setWidget(i, widget);
                i++;
            }
        }
    }

    @Override
    public String getCMD() {
        return "report_saving";
    }
}