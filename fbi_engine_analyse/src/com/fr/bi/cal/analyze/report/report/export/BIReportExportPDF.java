package com.fr.bi.cal.analyze.report.report.export;

import com.fr.base.Margin;
import com.fr.base.PaperSize;
import com.fr.bi.cal.analyze.report.BIReportor;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIReport;
import com.fr.main.workbook.PageRWorkBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.cell.FloatElement;
import com.fr.report.report.ResultReport;
import com.fr.report.worksheet.PageRWorkSheet;
import com.fr.stable.unit.MM;
import com.fr.web.core.SessionDealWith;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 全局导出pdf
 * Created by astronaut007 on 2017/6/21.
 */
public class BIReportExportPDF {

    private BISession session;
    protected BIReport report = new BIReportor();

    public BIReportExportPDF(String sessionID) throws Exception {
        this.session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
    }

    public PageRWorkBook getPDFExportBook (HttpServletRequest req) throws Exception {
        BIConvertWidgetsToFE biConvertWidgetsToFL = new BIConvertWidgetsToFE(session, req);
        ArrayList<FloatElement> floatElements = biConvertWidgetsToFL.getFloatElements();

        PageRWorkBook pageRWorkBook = new PageRWorkBook();
        PageRWorkSheet pageRWorkSheet = new PageRWorkSheet();

        if(floatElements.size() != 0) {
            for(FloatElement fl : floatElements) {
                pageRWorkSheet.addFloatElement(fl);
            }
        }

        pageRWorkBook.addReport("Dashboard", pageRWorkSheet);
        setPageSize(pageRWorkBook, biConvertWidgetsToFL.getPaperSize());
        return pageRWorkBook;
    }

    private void setPageSize(ResultWorkBook resultWorkBook, PaperSize paperSize) {
        for (int i = 0; i < resultWorkBook.getReportCount(); i++) {
            ResultReport report = resultWorkBook.getResultReport(i);
            report.getReportSettings().getPaperSetting().setPaperSize(paperSize);
            report.getReportSettings().getPaperSetting().setMargin(new Margin(new MM(0), new MM(0), new MM(0), new MM(0)));
        }
    }
}
