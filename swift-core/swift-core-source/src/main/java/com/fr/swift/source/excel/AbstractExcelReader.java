package com.fr.swift.source.excel;

import com.fr.stable.ColumnRow;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

import java.util.List;
import java.util.Map;

abstract class AbstractExcelReader {
    abstract List<Object[]> getRowDataList();

    abstract String[] getColumnNames();

    abstract ColumnType[] getColumnTypes();

    abstract Map<ColumnRow, ColumnRow> getMergeInfos();
}
