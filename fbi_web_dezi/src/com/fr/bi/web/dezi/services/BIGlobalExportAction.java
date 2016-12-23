package com.fr.bi.web.dezi.services;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.io.BIExcel2007Exporter;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.general.DateUtils;
import com.fr.io.exporter.AppExporter;
import com.fr.json.JSONObject;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.core.ReportUtils;
import com.fr.stable.StringUtils;
import com.fr.web.Browser;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.utils.ExportUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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

//        String reportId = WebUtils.getHTTPRequestParameter(req, "reportId");
        BIReportNode node = sessionIDInfo.getReportNode();
        String reportName = node.getReportName() != null ? node.getReportName() : "";

        if (StringUtils.isEmpty(reportName)) {
            return;
        }

        long t = System.currentTimeMillis();

        JSONObject reportSetting = BIReadReportUtils.getBIReportNodeJSON(node);
        JSONObject widgets = (JSONObject) reportSetting.get("widgets");
        String[] widgetNames = new String[widgets.length()];
        int i = 0;
        Iterator it = widgets.keys();
        while (it.hasNext()) {
            String wId = (String) it.next();
            JSONObject widget = widgets.getJSONObject(wId);
            widgetNames[i++] = BIWidgetFactory.parseWidget(widget, node.getUserId()).getWidgetName();
        }
        ResultWorkBook resultWorkBook = sessionIDInfo.getExportBoolByWidgetNames(widgetNames);
        String fileName = Browser.resolve(req).getEncodedFileName4Download(reportName.replace(",", "_").replaceAll("\\s", "_"));
        ExportUtils.setExcel2007Content(res, fileName);
        if (resultWorkBook != null) {
            export(req,res, resultWorkBook, t);
        }

        sessionIDInfo.getLoader().releaseCurrentThread();
    }

    private void export(HttpServletRequest req, HttpServletResponse res, ResultWorkBook resultWorkBook, long t) throws Exception {
        AppExporter exporter = new BIExcel2007Exporter(ReportUtils.getPaperSettingListFromWorkBook(resultWorkBook));
        OutputStream outputStream = res.getOutputStream();

        try{
            exporter.export(outputStream, resultWorkBook);
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
            ErrorHandlerHelper.getErrorHandler().error(req, res, e);
        } catch (Throwable k) {
            FRContext.getLogger().error(k.getMessage(), k);
        }

        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {

        }

        long t1 = System.currentTimeMillis();
        BILoggerFactory.getLogger().info("Export Excel: const " + DateUtils.miliisecondCostAsString(t1 - t));
    }
}