package com.fr.bi.conf.fs.develop.enviroment;


import com.fr.bi.stable.utils.code.BILogger;

import java.io.File;

/**
 * Created by Connery on 2015/1/4.
 */
public class BIGenerateExcel {
    public void generateExcels(String name, String password, String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        ReportIDRegister reportIDRegister = new ReportIDRegister();
        BIReportTestManager biReportTestManager = new BIReportTestManager(name, password);
        biReportTestManager.setReportIDRegister(reportIDRegister);
        try {
            biReportTestManager.generateExcels(path);
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }
}