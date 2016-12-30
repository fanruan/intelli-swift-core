package com.fr.bi.web.dezi.services;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.io.BIExcel2007Exporter;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.bi.web.dezi.phantom.utils.PhantomServerUtils;
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
import java.awt.image.BufferedImage;
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

        String chartOptions = "{\"options\" : {\"plotOptions\":{\"rotatable\":false,\"borderColor\":\"rgb(255,255,255)\",\"startAngle\":0,\"borderRadius\":0,\"borderWidth\":1,\"tooltip\":{\"formatter\":{\"identifier\":\"${SERIES}${VALUE}\",\"valueFormat\":\"function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}\",\"seriesFormat\":\"function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}\",\"percentFormat\":\"function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}\",\"categoryFormat\":\"function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}\"},\"shared\":false,\"padding\":5,\"backgroundColor\":\"rgba(0,0,0,0.4980392156862745)\",\"borderColor\":\"rgb(0,0,0)\",\"shadow\":true,\"borderRadius\":2,\"borderWidth\":0,\"follow\":false,\"enabled\":true,\"animation\":true},\"endAngle\":360,\"inverted\":false,\"innerRadius\":\"0.0%\",\"animation\":false},\"borderColor\":\"rgb(238,238,238)\",\"shadow\":false,\"legend\":{\"highlight\":true,\"borderColor\":\"rgb(204,204,204)\",\"visible\":true,\"borderRadius\":0,\"shadow\":false,\"borderWidth\":0,\"style\":{\"fontFamily\":\"微软雅黑\",\"color\":\"rgba(102,102,102,1.0)\",\"fontSize\":\"11.0pt\",\"fontWeight\":\"\"},\"position\":\"top\",\"enabled\":true},\"scale\":1,\"plotBorderColor\":\"rgb(238,238,238)\",\"title\":{\"borderRadius\":0,\"style\":{\"fontFamily\":\"微软雅黑\",\"color\":\"rgba(51,51,51,1.0)\",\"fontSize\":\"16.0pt\",\"fontWeight\":\"\"},\"useHtml\":false,\"text\":\"导出图片\",\"align\":\"center\"},\"tools\":{\"enabled\":false},\"plotBorderWidth\":0,\"colors\":[\"rgb(99,178,238)\",\"rgb(118,218,145)\",\"rgb(248,203,127)\",\"rgb(248,149,136)\",\"rgb(124,214,207)\",\"rgb(145,146,171)\"],\"borderRadius\":0,\"borderWidth\":0,\"series\":[{\"data\":[{\"x\":\"PS1\",\"y\":45},{\"x\":\"PS2\",\"y\":26},{\"x\":\"PS3\",\"y\":13},{\"x\":\"PS4\",\"y\":9},{\"x\":\"PS5\",\"y\":6},{\"x\":\"PS6\",\"y\":1}],\"name\":\"Pie1\",\"type\":\"pie\"}],\"chartType\":\"pie\",\"style\":\"gradual\",\"plotShadow\":false,\"plotBorderRadius\":0}}";
        String base64 = PhantomServerUtils.postMessage("127.0.0.1", 8089, chartOptions);
        //base64转image对象
        BufferedImage img = PhantomServerUtils.base64Decoder(base64);

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
        ResultWorkBook resultWorkBook = sessionIDInfo.getExportBookByWidgetNames(widgetNames);
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