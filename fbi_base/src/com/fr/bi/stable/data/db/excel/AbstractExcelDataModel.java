package com.fr.bi.stable.data.db.excel;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.data.AbstractDataModel;
import com.fr.general.DateUtils;
import com.fr.general.data.TableDataException;
import com.fr.stable.ColumnRow;

import java.util.List;
import java.util.Map;

/**
 * Created by zcf on 2016/11/21.
 */
public abstract class AbstractExcelDataModel extends AbstractDataModel {
    private final static int EXCEL_TYPE_XLS = 1;
    private final static int EXCEL_TYPE_XLSX = 2;
    private final static int EXCEL_TYPE_CSV = 3;

    //列名
    protected String[] columnNames;
    //表数据
    protected List<Object[]> rowDataList;
    //字段类型
    protected int[] columnTypes;
    //合并规则
    protected Map<ColumnRow, ColumnRow> mergeInfos;

    protected String filePath;

    private boolean isDataInit = false;

    public AbstractExcelDataModel(String filePath, String[] columnNames, int[] columnTypes) {
        this.filePath = filePath;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    public AbstractExcelDataModel(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public int getColumnCount() throws TableDataException {
        if (this.columnNames == null) {
            initData();
        }
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) throws TableDataException {
        return this.columnNames[columnIndex];
    }

    public int getColumnType(int columnIndex) throws TableDataException {
        return this.columnTypes[columnIndex];
    }

    @Override
    public int getRowCount() throws TableDataException {
        if (this.rowDataList == null) {
            initData();
        }
        return this.rowDataList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) throws TableDataException {
        initData();
        if (rowIndex > rowDataList.size() - 1 || columnIndex > rowDataList.get(rowIndex).length - 1) {
            return null;
        }
        return rowDataList.get(rowIndex)[columnIndex];
    }

    public Object getValueAt4Preview(int rowIndex, int columnIndex) throws Exception {
        if (rowIndex > rowDataList.size() - 1 || columnIndex > rowDataList.get(rowIndex).length - 1) {
            return null;
        }
        return rowDataList.get(rowIndex)[columnIndex];
    }

    public String[] onlyGetColumnNames() throws TableDataException {
        if (this.columnNames == null) {
            initPartData();
        }
        return columnNames;
    }

    public int[] onlyGetColumnTypes() throws TableDataException {
        if (this.columnTypes == null) {
            initPartData();
        }
        return columnTypes;
    }

    public List<Object[]> getDataList() {
        if (this.rowDataList == null) {
            initData();
        }
        return rowDataList;
    }

    public Map<ColumnRow, ColumnRow> getMergeInfos() {
        if (this.mergeInfos == null) {
            initData();
        }
        return mergeInfos;
    }

    /**
     * 释放
     *
     * @throws Exception
     */
    @Override
    public void release() throws Exception {

    }

    /**
     * 初始化，全部数据
     */
    private void initData() {
        if (isDataInit) {
            return;
        }
        switch (getExcelType()) {
            case EXCEL_TYPE_CSV:
                initExcel4CSV(false);
                break;
            case EXCEL_TYPE_XLS:
                initExcel4XLS(false);
                break;
            case EXCEL_TYPE_XLSX:
                initExcel4XLSX(false);
                break;
        }
        isDataInit = true;
    }

    /**
     * 初始化，获取部分
     */
    private void initPartData() {
        if (this.rowDataList != null) {
            return;
        }
        switch (getExcelType()) {
            case EXCEL_TYPE_CSV:
                initExcel4CSV(true);
                break;
            case EXCEL_TYPE_XLS:
                initExcel4XLS(true);
                break;
            case EXCEL_TYPE_XLSX:
                initExcel4XLSX(true);
                break;
        }
    }

    private int getExcelType() {
        if (this.filePath.endsWith(".xls")) {
            return EXCEL_TYPE_XLS;
        }
        if (this.filePath.endsWith(".xlsx")) {
            return EXCEL_TYPE_XLSX;
        }
        return EXCEL_TYPE_CSV;
    }

    /**
     * 初始化07excel
     */
    protected abstract void initExcel4XLSX(boolean isPreview);

    /**
     * 初始化03excel
     */
    protected abstract void initExcel4XLS(boolean isPreview);

    private void initExcel4CSV(boolean isPreview) {
        long start = System.currentTimeMillis();
        try {
            ExcelCSVUtil excelCSVUtil = new ExcelCSVUtil(this.filePath, isPreview);
            BILoggerFactory.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excelCSVUtil.getRowDataList();
            columnNames = excelCSVUtil.getColumnNames();
            columnTypes = excelCSVUtil.getColumnTypes();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
    }
}
