package com.fr.bi.cal.report.main.bianalysis;

import com.fr.bi.cal.report.report.BIAnalyReport;
import com.fr.main.workbook.ResultWorkBook;

/**
 * @author:ben Administrator
 * @time: 2012 2012-7-3
 * @description:此类用于
 */
public interface BIAnalyWorkBook extends ResultWorkBook {
    /**
     * @param reportName
     * @param report     添加结果
     */
    public void addReport(String reportName, BIAnalyReport report);

    /**
     * @param index 指定的索引
     * @return 返回其中报表
     */
    public BIAnalyReport getBIAnalyReport(int index);
}