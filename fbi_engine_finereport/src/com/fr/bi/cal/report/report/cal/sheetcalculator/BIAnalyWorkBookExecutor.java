package com.fr.bi.cal.report.report.cal.sheetcalculator;

import com.fr.bi.cal.report.main.bianalysis.BIAnalyResultWorkBook;
import com.fr.bi.cal.report.report.BIAnalyReport;
import com.fr.bi.cal.report.report.BITemplateReport;
import com.fr.main.impl.WorkBook;
import com.fr.main.workbook.ResultWorkBook;

import java.util.Map;

/**
 * @author:ben Administrator
 * @time: 2012 2012-7-3r
 * @description:此类用于
 */
public class BIAnalyWorkBookExecutor {

    private BIAnalyReport res;
    private BITemplateReport tr;
    private WorkBook workBook;
    private Map parameterMap;
    private String reportName;


    public BIAnalyWorkBookExecutor(WorkBook workBook, Map parameterMap) {
        init(workBook, parameterMap);
    }

    public BIAnalyResultWorkBook execute() {

        this._execute();
        BIAnalyResultWorkBook book = null;

        book = new BIAnalyResultWorkBook(parameterMap);
        dealWithExecutedAttr(book);
        if (res != null) {
            book.addReport(reportName, res);
        }
        return book;
    }

    protected void _execute() {
        if (tr != null) {
            res = tr.execute4BI(parameterMap);
        }
    }

    void init(WorkBook workBook, Map parameterMap) {
        this.workBook = workBook;
        this.parameterMap = parameterMap;
        //只有1个
        tr = (BITemplateReport) workBook.getTemplateReport(0);
        reportName = workBook.getReportName(0);

    }

    protected void dealWithExecutedAttr(ResultWorkBook resultBook) {
        if (resultBook == null) {
            return;
        }

        if (workBook.getReportWebAttr() != null) {
            resultBook.setReportWebAttr(workBook.getReportWebAttr());
        }

        if (workBook.getReportExportAttr() != null) {
            resultBook.setReportExportAttr(workBook.getReportExportAttr());
        }
    }

}