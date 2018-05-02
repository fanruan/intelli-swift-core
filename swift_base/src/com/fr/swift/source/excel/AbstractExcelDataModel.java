package com.fr.swift.source.excel;

import com.fr.data.AbstractDataModel;
import com.fr.general.DateUtils;
import com.fr.stable.ColumnRow;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zcf on 2016/11/21.
 */
@Deprecated
public abstract class AbstractExcelDataModel extends AbstractDataModel {
    public final static int EXCEL_TYPE_XLS = 1;
    public final static int EXCEL_TYPE_XLSX = 2;
    public final static int EXCEL_TYPE_CSV = 3;

    protected AbstractExcelReader excelUtils;
    //列名
    protected String[] columnNames;
    //表数据
    protected List<Object[]> rowDataList;
    //字段类型
    protected ColumnType[] columnTypes;
    //合并规则
    protected Map<ColumnRow, ColumnRow> mergeInfos;

    protected String filePath;

    private boolean isDataInit = false;

    private boolean end = false;

    private Map<Integer, Object[]> csvRow = new HashMap<Integer, Object[]>();

    public AbstractExcelDataModel(String filePath, String[] columnNames, ColumnType[] columnTypes) {
        this.filePath = filePath;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    public AbstractExcelDataModel(String filePath) {
        this.filePath = filePath;
    }

    public boolean isEnd() {
        return end;
    }

    @Override
    public int getColumnCount() {
        if (this.columnNames == null) {
            initData();
        }
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return this.columnNames[columnIndex];
    }

    public ColumnType getColumnType(int columnIndex) {
        return this.columnTypes[columnIndex];
    }

    @Override
    public int getRowCount() {
        if (this.rowDataList == null) {
            initData();
        }
        return this.rowDataList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        initData();
        if (getExcelType() == EXCEL_TYPE_CSV) {
            return getValueAt4CSV(rowIndex, columnIndex);
        } else {
            if (rowIndex > rowDataList.size() - 1 || columnIndex > rowDataList.get(rowIndex).length - 1) {
                return null;
            }
            return rowDataList.get(rowIndex)[columnIndex];
        }
    }

    private Object getValueAt4CSV(int rowIndex, int columnIndex) {
        try {
            if (csvRow.containsKey(rowIndex)) {
                return csvRow.get(rowIndex)[columnIndex];
            } else {
                csvRow.clear();
                Object[] row = ((ExcelCSVReader) excelUtils).read();
                end = ((ExcelCSVReader) excelUtils).isEnd();
                csvRow.put(rowIndex, row);
                return row[columnIndex];
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    public Object getValueAt4Preview(int rowIndex, int columnIndex) {
        if (rowIndex > rowDataList.size() - 1 || columnIndex > rowDataList.get(rowIndex).length - 1) {
            return null;
        }
        return rowDataList.get(rowIndex)[columnIndex];
    }

    public String[] onlyGetColumnNames() {
        if (this.columnNames == null) {
            initPartData();
        }
        return columnNames;
    }

    public ColumnType[] onlyGetColumnTypes() {
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
        if (this.mergeInfos == null && this.getExcelType() != EXCEL_TYPE_CSV) {
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
    public void release() {

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
            default:
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
            default:
                initExcel4XLSX(true);
                break;
        }
    }

    public int getExcelType() {
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
            excelUtils = new ExcelCSVReader(this.filePath, isPreview);
            SwiftLoggers.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excelUtils.getRowDataList();
            columnNames = excelUtils.getColumnNames();
            columnTypes = excelUtils.getColumnTypes();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }
    }
}
