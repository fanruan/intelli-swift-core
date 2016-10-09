package com.fr.bi.conf.fs.develop.enviroment;


import com.fr.bi.conf.fs.develop.DeveloperConfig;
import com.finebi.cube.common.log.BILoggerFactory;

/**
 * Created by Connery on 2015/1/4.
 */
public class TestRunner implements Runnable {
    private String name;
    private String password;

    public TestRunner(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public void run() {
        ReportIDRegister reportIDRegister = new ReportIDRegister();
        BIReportTestManager biReportTestManager = new BIReportTestManager(name, password);
        biReportTestManager.setReportIDRegister(reportIDRegister);
        try {
            biReportTestManager.generateExcels(DeveloperConfig.getTestBasicPath());
        } catch (Exception ex) {
            BILoggerFactory.getLogger().error(ex.getMessage(), ex);
        }
    }
}