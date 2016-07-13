package com.fr.bi.web.dezi.services;

import com.fr.base.ExcelUtils;
import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.io.BIExcel2007Exporter;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.data.NetworkHelper;
import com.fr.general.DateUtils;
import com.fr.general.DeclareRecordType;
import com.fr.io.exporter.AppExporter;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.core.ReportUtils;
import com.fr.stable.StringUtils;
import com.fr.web.Browser;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.utils.ExportUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author:ben Administrator
 * @time: 2012 2012-9-4
 * @description:此类用于
 */
public class BIExcelExportAction extends AbstractBIDeziAction {

    public static final String CMD = "bi_export_excel";

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        BISession sessionIDInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (sessionIDInfor == null) {
            ErrorHandlerHelper.getErrorHandler().error(req, res, "Reportlet SessionID: \"" + sessionID + "\" time out.");
            return;
        }

        String blockid = WebUtils.getHTTPRequestParameter(req, "name");
        if (StringUtils.isEmpty(blockid)) {
            return;
        }
        long t = System.currentTimeMillis();
        NetworkHelper.setCacheSettings(res);
        ResultWorkBook resultBook = sessionIDInfor.getExportBookByName(blockid);
        String fileName = Browser.resolve(req).getEncodedFileName4Download(blockid.replaceAll(",", "_").replaceAll("\\s", "_"));
        ExportUtils.setExcel2007Content(res, fileName);
        if (resultBook != null) {
            export(req, res, resultBook, t);
        }
        sessionIDInfor.getLoader().releaseCurrentThread();
        //pony 先不删吧...强制转换抛错....
        //     sessionIDInfor.removeExcelWidget(((BIReport)(resultBook.getReport(0))).getWidgetName(0));
    }

    // b:TODO 现在只考虑原样导出，但是有可能存在超大数据量的表格，那是就必须考虑多页多文件导出
    private void export(HttpServletRequest req, HttpServletResponse res, ResultWorkBook resultBook, long t) throws Exception {
        // b:papersetting 无用，这里的exporter是针对BI的特殊的
        AppExporter exporter = new BIExcel2007Exporter(ReportUtils.getPaperSettingListFromWorkBook(resultBook));
        DeclareRecordType exportType = DeclareRecordType.EXPORT_TYPE_EXCEL_ORIGINAL;
        OutputStream outputStream = res.getOutputStream();
        try {
            exporter.export(outputStream, resultBook);
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
            ErrorHandlerHelper.getErrorHandler().error(req, res, e);
        }

        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            // alex:有些Exporter在export里面可能会已经做了out.close操作,为了不打印IOException,这里catch到之后不输出
        }
        long t1 = System.currentTimeMillis();
        BILogger.getLogger().info("Export Excel: cost: " + DateUtils.miliisecondCostAsString(t1 - t) );
    }
}