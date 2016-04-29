package com.fr.bi.conf.fs.develop.enviroment;

import com.fr.bi.conf.fs.develop.utility.ReportUitlity;

import java.util.Iterator;

/**
 * Created by Connery on 2015/1/3.
 */
public class BIReportTestManager {
    private BIReportOperation biReportOperation;
    private ReportIDRegister reportIDRegister;

    public BIReportTestManager(String name, String psw) {
        biReportOperation = new BIReportOperation();
        try {
            biReportOperation.login(name, psw);

        } catch (Exception ex) {

        }
    }

    public void setReportIDRegister(ReportIDRegister reportIDRegister) {
        this.reportIDRegister = reportIDRegister;
    }

    public void generateExcels(String absolutePath) throws Exception {
        WidgetDataRecorder widgetDataRecorder = new WidgetDataRecorder();
        Iterator<String> reportIDsIt = reportIDRegister.getReportIDList().iterator();
        while (reportIDsIt.hasNext()) {
            String id = reportIDsIt.next();
            String config = widgetDataRecorder.readData(id);
            byte[] excelContent = biReportOperation.getWidgetExcel(id, config, "%E7%BB%9F%E8%AE%A1%E7%BB%84%E4%BB%B6");
            ReportUitlity.writeExcel(excelContent, absolutePath + id + ".xls");
        }
    }

    public void testExcels(String absolutePath) throws Exception {
        byte[] excelContent = biReportOperation.excel();
        ReportUitlity.writeExcel(excelContent, absolutePath + "temp" + ".xls");
    }
}