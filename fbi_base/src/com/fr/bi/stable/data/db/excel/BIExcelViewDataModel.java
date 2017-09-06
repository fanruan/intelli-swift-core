package com.fr.bi.stable.data.db.excel;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.DateUtils;

/**
 * Created by zcf on 2016/11/21.
 */
public class BIExcelViewDataModel extends AbstractExcelDataModel {

    public BIExcelViewDataModel(String filePath, String[] columnNames, int[] columnTypes) {
        super(filePath, columnNames, columnTypes);
    }

    public BIExcelViewDataModel(String filePath) {
        super(filePath);
    }

    /**
     * 初始化07excel
     */
    protected void initExcel4XLSX(boolean isPreview) {
        long start = System.currentTimeMillis();
        try {
            ExcelView2007Reader excelView2007Util = new ExcelView2007Reader(this.filePath, isPreview);
            BILoggerFactory.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excelView2007Util.getRowDataList();
            columnNames = excelView2007Util.getColumnNames();
            columnTypes = excelView2007Util.getColumnTypes();
            mergeInfos = excelView2007Util.getMergeInfos();
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
            ExcelView2003Reader excelView2003Util = new ExcelView2003Reader(this.filePath, isPreview);
            BILoggerFactory.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excelView2003Util.getRowDataList();
            columnNames = excelView2003Util.getColumnNames();
            columnTypes = excelView2003Util.getColumnTypes();
            mergeInfos = excelView2003Util.getMergeInfos();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
    }
}
