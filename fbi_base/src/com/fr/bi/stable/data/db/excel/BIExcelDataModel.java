package com.fr.bi.stable.data.db.excel;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.DateUtils;

/**
 * Created by sheldon on 14-8-8.
 */
public class BIExcelDataModel extends AbstractExcelDataModel {

    public BIExcelDataModel(String filePath, String[] columnNames, int[] columnTypes) {
        super(filePath, columnNames, columnTypes);
    }

    public BIExcelDataModel(String filePath) {
        super(filePath);
    }

    /**
     * 初始化07excel
     */
    protected void initExcel4XLSX(boolean isPreview) {
        long start = System.currentTimeMillis();
        try {
            excelUtils = new Excel2007Reader(this.filePath, isPreview);
            BILoggerFactory.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excelUtils.getRowDataList();
            columnNames = excelUtils.getColumnNames();
            columnTypes = excelUtils.getColumnTypes();
            mergeInfos = excelUtils.getMergeInfos();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
    }

    /**
     * 初始化03excel
     */
    protected void initExcel4XLS(boolean isPreview) {
        long start = System.currentTimeMillis();
        try {
            Excel2003Reader excel2003Util = new Excel2003Reader(this.filePath, isPreview);
            BILoggerFactory.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excel2003Util.getRowDataList();
            columnNames = excel2003Util.getColumnNames();
            columnTypes = excel2003Util.getColumnTypes();
            mergeInfos = excel2003Util.getMergeInfos();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
    }
}
