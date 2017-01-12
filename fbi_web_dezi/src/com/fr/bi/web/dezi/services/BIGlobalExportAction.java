package com.fr.bi.web.dezi.services;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.BIReportExportExcel;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.io.BIExcel2007Exporter;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.general.DateUtils;
import com.fr.io.exporter.AppExporter;
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

        BIReportNode node = sessionIDInfo.getReportNode();
        String reportName = node.getReportName() != null ? node.getReportName() : "";

        if (StringUtils.isEmpty(reportName)) {
            return;
        }

        long t = System.currentTimeMillis();

        BIReportExportExcel biReportExportExcel = new BIReportExportExcel(sessionID);
        ResultWorkBook resultWorkBook = biReportExportExcel.getExportBook();
        String fileName = Browser.resolve(req).getEncodedFileName4Download(reportName.replace(",", "_").replaceAll("\\s", "_"));
        ExportUtils.setExcel2007Content(res, fileName);
        if (resultWorkBook != null) {
            export(req, res, resultWorkBook, t);
        }

        sessionIDInfo.getLoader().releaseCurrentThread();
    }

    private void export(HttpServletRequest req, HttpServletResponse res, ResultWorkBook resultWorkBook, long t) throws Exception {
        AppExporter exporter = new BIExcel2007Exporter(ReportUtils.getPaperSettingListFromWorkBook(resultWorkBook));
        OutputStream outputStream = res.getOutputStream();

        try {
            exporter.export(outputStream, resultWorkBook);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            ErrorHandlerHelper.getErrorHandler().error(req, res, e);
        } catch (Throwable k) {
            BILoggerFactory.getLogger().error(k.getMessage(), k);
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