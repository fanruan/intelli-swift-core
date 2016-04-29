package com.fr.bi.conf.fs.develop.test;


import com.fr.bi.conf.fs.develop.enviroment.BIGenerateExcel;
import com.fr.bi.conf.fs.develop.utility.ExcelComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Connery on 2015/1/4.
 */
public class CurrentCodeInspector {

    private String standardPath;
    private String currentPath;

    public CurrentCodeInspector(String standardPath, String currentPath) {
        this.standardPath = standardPath;
        this.currentPath = currentPath;
    }

    public static void main(String[] arg) {
        CurrentCodeInspector currentCodeInspector = new CurrentCodeInspector(InspectorConfig.STANDARD_PATH, InspectorConfig.TEMP_PATH);
        currentCodeInspector.runTest();
    }

    public String getStandardPath() {
        return standardPath;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void runTest() {

        generateCurrentResult();
        ArrayList<String> result = compare(getStandardExcelName(), getCurrentExcelName());
        if (result.isEmpty()) {
            System.out.println("测试通过");
        } else {
            Iterator<String> it = result.iterator();
            while (it.hasNext()) {
                System.out.println(it.next() + " 号模板测试未通过");
            }
        }
    }

    private ArrayList<String> compare(List<String> standardChildren, List<String> currentChildren) {
        ExcelComparator excelComparator = new ExcelComparator();
        Iterator<String> it = standardChildren.iterator();
        ArrayList<String> failResult = new ArrayList<String>();
        while (it.hasNext()) {
            String excelName = it.next();
            if (currentChildren.contains(excelName)) {
                try {
                    if (!excelComparator.isExcelEqual(standardPath + excelName, currentPath + excelName)) {
                        failResult.add(excelName);
                    }
                } catch (Exception ex) {

                }

            }
        }
        return failResult;
    }

    protected List<String> getStandardExcelName() {
        return getChildrenFileName(standardPath);
    }

    protected List<String> getCurrentExcelName() {
        return getChildrenFileName(currentPath);
    }

    public List<String> getChildrenFileName(String path) {
        File standard = new File(path);
        return Arrays.asList(standard.list());
    }

    protected void generateCurrentResult() {
        BIGenerateExcel biGenerateExcel = new BIGenerateExcel();
        biGenerateExcel.generateExcels(InspectorConfig.NAME, InspectorConfig.PSW, currentPath);
    }
}