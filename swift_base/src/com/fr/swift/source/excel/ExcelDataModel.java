package com.fr.swift.source.excel;

import com.fr.general.DateUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

/**
 * Created by sheldon on 14-8-8.
 */
@Deprecated
public class ExcelDataModel extends AbstractExcelDataModel {

    public ExcelDataModel(String filePath, String[] columnNames, ColumnType[] columnTypes) {
        super(filePath, columnNames, columnTypes);
    }

    public ExcelDataModel(String filePath) {
        super(filePath);
    }

    /**
     * 初始化07excel
     */
    @Override
    protected void initExcel4XLSX(boolean isPreview) {
        long start = System.currentTimeMillis();
        try {
            excelUtils = new Excel2007Reader(this.filePath, isPreview);
            SwiftLoggers.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excelUtils.getRowDataList();
            columnNames = excelUtils.getColumnNames();
            columnTypes = excelUtils.getColumnTypes();
            mergeInfos = excelUtils.getMergeInfos();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }
    }

    /**
     * 初始化03excel
     */
    @Override
    protected void initExcel4XLS(boolean isPreview) {
        long start = System.currentTimeMillis();
        try {
            Excel2003Reader excel2003Util = new Excel2003Reader(this.filePath, isPreview);
            SwiftLoggers.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excel2003Util.getRowDataList();
            columnNames = excel2003Util.getColumnNames();
            columnTypes = excel2003Util.getColumnTypes();
            mergeInfos = excel2003Util.getMergeInfos();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }
    }
}
