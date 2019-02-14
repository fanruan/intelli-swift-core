package com.fr.swift.source.excel.data;

import com.fr.data.AbstractDataModel;
import com.fr.general.DateUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.excel.ExcelCSVReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/4/26
 */
public class CSVDataModel extends AbstractDataModel implements IExcelDataModel {

    protected List<Object[]> rowDataList;
    private String[] columnNames;
    private ColumnTypeConstants.ColumnType[] columnTypes;
    private Map<Integer, Object[]> csvRow = new HashMap<Integer, Object[]>();
    private ExcelCSVReader excelUtils;
    private boolean end = false;
    private boolean isDataInit = false;
    private String filePath;
    private InputStream inputStream;
    private int rowCount;

    CSVDataModel(InputStream inputStream, String filePath) {
        this.filePath = filePath;
        this.inputStream = inputStream;
    }

    CSVDataModel(String filePath) {
        this.filePath = filePath;
    }

    CSVDataModel(String filePath, String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes) {
        this.filePath = filePath;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    CSVDataModel(InputStream inputStream, String filePath, String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes) {
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.filePath = filePath;
        this.inputStream = inputStream;
    }

    @Override
    public int getColumnCount() {
        if (this.columnNames == null) {
            initExcel4CSV(true);
        }
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int i) {
        if (null == columnNames || columnNames.length == 0) {
            initExcel4CSV(false);
        }
        return this.columnNames[i];
    }

    @Override
    public int getRowCount() {
        initData();
        return rowDataList.isEmpty() ? rowCount : rowDataList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        initData();
        try {
            if (csvRow.containsKey(rowIndex)) {
                return csvRow.get(rowIndex)[columnIndex];
            } else {
                // 这为啥要clear
//                csvRow.clear();
                Object[] row = excelUtils.read();
                end = excelUtils.isEnd();
                if (null != row && row.length == columnNames.length) {
                    csvRow.put(rowIndex, row);
                    return row[columnIndex];
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String[] onlyGetColumnNames() {
        if (null == columnNames || columnNames.length == 0) {
            initExcel4CSV(true);
        }
        return columnNames;
    }

    private void initExcel4CSV(boolean isPreview) {
        long start = System.currentTimeMillis();
        try {
            if (null != inputStream) {
                excelUtils = new ExcelCSVReader(inputStream, this.filePath, isPreview);
            } else {
                excelUtils = new ExcelCSVReader(this.filePath, isPreview);
            }
            SwiftLoggers.getLogger().info("read excel time : " + DateUtils.timeCostFrom(start));
            rowDataList = excelUtils.getRowDataList();
            rowCount = rowDataList.size();
            if (null == columnNames || columnNames.length == 0) {
                columnNames = excelUtils.getColumnNames();
            }
            if (null == columnTypes || columnTypes.length == 0) {
                columnTypes = excelUtils.getColumnTypes();
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }
    }

    @Override
    public ColumnTypeConstants.ColumnType[] onlyGetColumnTypes() {
        if (null == columnTypes || columnTypes.length == 0) {
            initExcel4CSV(true);
        }
        return columnTypes;
    }

    @Override
    public ColumnTypeConstants.ColumnType getColumnType(int i) {
        if (null == columnTypes) {
            onlyGetColumnTypes();
        }
        return columnTypes[i];
    }

    private void initData() {
        if (isDataInit) {
            return;
        }
        initExcel4CSV(false);
        isDataInit = true;
    }

    public boolean isEnd() {
        return end;
    }

    @Override
    public boolean hasRow(int i) {
        if (null != getValueAt(i, 0) || !isEnd()) {
            // i从0开始所以要加一
            rowCount = i + 1;
            return true;
        }
        return false;
    }
}
