package com.fr.bi.cal.report.report;

import com.fr.base.DynamicUnitList;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.page.ReportPageAttrProvider;
import com.fr.report.elementcase.ElementCase;
import com.fr.report.poly.AbstractResPolyReport;
import com.fr.stable.xml.XMLPrintWriter;

import java.util.Iterator;

/**
 * @author:ben Administrator
 * @time: 2012 2012-7-3
 * @description:ex后的包含resultblock的resultreport，getblock，getresultblock等等需要重构
 */

public class BIAnalyReport extends AbstractResPolyReport {
    /**
     *
     */
    private static final long serialVersionUID = 5175409084239791841L;

    @Override
    public ResultWorkBook getResultWorkBook() {
        return (ResultWorkBook) super.getBook();
    }

    @Override
    public void setResultWorkBook(ResultWorkBook resultWorkBook) {
        super.setBook(resultWorkBook);
    }

    //b:TODO special 4 bi workbook；
    public int getColumnCount() {
        return ((ElementCase) this.getBlock(0)).getColumnCount();
    }

    @Override
    public DynamicUnitList getRowHeightList_DEC() {
        return null;
    }

    @Override
    public DynamicUnitList getColumnWidthList_DEC() {
        return null;
    }

    @Override
    public Iterator cellIterator() {
        return null;
    }

    public int getRowCount() {
        return ((ElementCase) this.getBlock(0)).getRowCount();
    }

    @Override
    public ReportPageAttrProvider getReportPageAttr() {
        return null;
    }

    @Override
    public void writeCommonXML(XMLPrintWriter writer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}