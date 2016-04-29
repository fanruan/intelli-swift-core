package com.fr.bi.cal.report.main.bianalysis;

import com.fr.bi.cal.report.report.BIAnalyReport;
import com.fr.main.workbook.AbstractResWorkBook;
import com.fr.stable.xml.XMLPrintWriter;

import java.io.InputStream;
import java.util.Map;

/**
 * @author:ben Administrator
 * @time: 2012 2012-7-3
 * @description:此类用于
 */
public class BIAnalyResultWorkBook extends AbstractResWorkBook implements BIAnalyWorkBook {

    /**
     *
     */
    private static final long serialVersionUID = 2738989436639653339L;

    public BIAnalyResultWorkBook(Map executeParamMap) {
        super(executeParamMap);
    }

    @Override
    public void addReport(String reportName, BIAnalyReport report) {
        super.addReport(reportName, report);
    }

    @Override
    public BIAnalyReport getBIAnalyReport(int index) {
        return (BIAnalyReport) super.getResultReport(index);
    }

    @Override
    protected void mainContentWriteXML(XMLPrintWriter writer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void readStream(InputStream in) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String openTag() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //b:确保生成的cells被移除
    public void clear() {

    }
}