package com.fr.bi.conf.fs.develop.test;


import com.fr.bi.conf.fs.develop.enviroment.BIGenerateExcel;

import java.io.File;

/**
 * Created by Connery on 2015/1/4.
 */
public class GenerateStandardResult {

    public static void main(String[] arg) {

        File file = new File(InspectorConfig.STANDARD_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        BIGenerateExcel biGenerateExcel = new BIGenerateExcel();
        biGenerateExcel.generateExcels(InspectorConfig.NAME, InspectorConfig.PSW, InspectorConfig.STANDARD_PATH);
    }
}