package com.fr.swift.source.excel.data;

import com.fr.data.impl.excelplus.ExcelDataModelPlus;
import com.fr.general.DateUtils;
import com.fr.general.GeneralUtils;
import com.fr.general.data.TableDataException;
import com.fr.stable.StringUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.excel.ExcelConstant;

import java.io.InputStream;
import java.util.Date;

/**
 * @author yee
 * @date 2018/4/26
 */
public class ExcelDataModel extends ExcelDataModelPlus implements IExcelDataModel {

    private String[] columnNames;
    private ColumnTypeConstants.ColumnType[] columnTypes;


    ExcelDataModel(InputStream inputStream, String filePath) {
        super(inputStream, filePath, true, null);
    }

    ExcelDataModel(String filePath) {
        super(filePath, true, null);
    }

    ExcelDataModel(String filePath, String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes) {
        this(filePath);
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    ExcelDataModel(InputStream inputStream, String filePath, String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes) {
        this(inputStream, filePath);
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    @Override
    public String[] onlyGetColumnNames() {
        if (null == columnNames || columnNames.length == 0) {
            try {
                int columnCount = getColumnCount();
                columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = getColumnName(i);
                }
            } catch (TableDataException e) {
                columnNames = new String[0];
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        return columnNames;
    }

    public ColumnTypeConstants.ColumnType getColumnType(int i) {
        if (null == columnTypes) {
            onlyGetColumnTypes();
        }
        return i < columnTypes.length ? columnTypes[i] : ColumnTypeConstants.ColumnType.STRING;
    }

    @Override
    public ColumnTypeConstants.ColumnType[] onlyGetColumnTypes() {
        if (null == columnTypes || columnTypes.length == 0) {
            try {
                int columnCount = getColumnCount();
                columnTypes = new ColumnTypeConstants.ColumnType[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    String v;
                    try {
                        v = GeneralUtils.objectToString(getValueAt(0, j));
                    } catch (Exception e) {
                        v = StringUtils.EMPTY;
                    }
                    boolean dateType = false;
                    try {
                        Date date = DateUtils.string2Date(v, true);
                        if (date != null) {
                            dateType = true;
                        }
                    } catch (Exception e) {
                        dateType = false;
                    }
                    if (v.matches(ExcelConstant.NUMBER_REG)) {
                        columnTypes[j] = ColumnTypeConstants.ColumnType.NUMBER;
                    } else if (dateType) {
                        columnTypes[j] = ColumnTypeConstants.ColumnType.DATE;
                    } else {
                        columnTypes[j] = ColumnTypeConstants.ColumnType.STRING;
                    }
                }
            } catch (TableDataException e) {
                columnTypes = new ColumnTypeConstants.ColumnType[0];
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        return columnTypes;
    }
}
