package com.fr.bi.web.report.services.finecube;

import com.fr.base.TableData;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.utils.CubeReadingUtils;
import com.fr.bi.cal.analyze.executor.table.GroupExecutor;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.dav.DavXMLUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Created by Young's on 2016/9/8.
 */
public class FCLoadDataAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "fc_load_data";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        OutputStream out = res.getOutputStream();

        // 用DavXMLUtils.writeXMLFileTableData将数据集写入输出流
        // 报表端读取可以用DavXMLUtils.readXMLTableData读取
        DavXMLUtils.writeXMLFileTableData(getTableData(req, sessionID), out);
        out.flush();
        out.close();
    }

    private TableData getTableData(HttpServletRequest req, String sessionID) throws Exception {
        BISession session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        long userId = session.getUserIdFromSession(req);
        JSONObject json = new JSONObject(WebUtils.getHTTPRequestParameter(req, "widget"));
        String widgetName = json.optString("name");
        json.put("sessionId", sessionID);
        BIWidget widget = BIWidgetFactory.parseWidget(json, userId);
        BIReport biReport = session.getBIReport();
        int index = biReport.getWidgetIndexByName(widgetName);
        biReport.setWidget(index, widget);
        TableWidget tableWidget = (TableWidget) widget;
        Node node = ((GroupExecutor) tableWidget.getExecutor(session)).getCubeNode();
        BISummaryTarget[] summary = tableWidget.getViewTargets();
        BIDimension[] rows = tableWidget.getViewDimensions();
        return CubeReadingUtils.createChartTableData(node, rows, summary);
    }
}
