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

    private BIAnalyReport[] res;
    private BITemplateReport[] tr;
    private WorkBook workBook;
    private Map parameterMap;
    private String[] reportName;


    public BIAnalyWorkBookExecutor(WorkBook workBook, Map parameterMap) {
        init(workBook, parameterMap);
    }

    public BIAnalyResultWorkBook execute() {

        this._execute();
        BIAnalyResultWorkBook book = null;

        book = new BIAnalyResultWorkBook(parameterMap);
        dealWithExecutedAttr(book);
        if (res.length != 0) {
            for(int i = 0; i < res.length; i ++) {
                book.addReport(reportName[i], res[i]);
            }
        }
        return book;
    }

    protected void _execute() {
        if (tr.length != 0) {
            for (int i = 0; i < tr.length; i++){
                res[i] = tr[i].execute4BI(parameterMap);
            }
        }
    }

    void init(WorkBook workBook, Map parameterMap) {
        int size = workBook.getReportCount();
        this.workBook = workBook;
        this.parameterMap = parameterMap;
        this.res = new BIAnalyReport[size];
        this.tr = new BITemplateReport[size];
        this.reportName = new String[size];
        for (int i = 0; i < size; i++) {
            tr[i] = (BITemplateReport) workBook.getTemplateReport(i);
            reportName[i] = workBook.getReportName(i);
        }
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