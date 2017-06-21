package com.fr.bi.cal.analyze.report.report.export;

import com.fr.bi.cal.analyze.report.BIReportor;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIReport;
import com.fr.main.workbook.PageRWorkBook;
import com.fr.report.cell.FloatElement;
import com.fr.report.worksheet.PageRWorkSheet;
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
        BIConvertWidgetsToFL biConvertWidgetsToFL = new BIConvertWidgetsToFL(session, req);
        ArrayList<FloatElement> floatElements = biConvertWidgetsToFL.getFloatElements();

        PageRWorkBook pageRWorkBook = new PageRWorkBook();
        PageRWorkSheet pageRWorkSheet = new PageRWorkSheet();

        if(floatElements.size() != 0) {
            for(FloatElement fl : floatElements) {
                pageRWorkSheet.addFloatElement(fl);
            }
        }

        pageRWorkBook.addReport("Dashboard", pageRWorkSheet);
        return pageRWorkBook;
    }
}
