package com.fr.swift.source.excel;

import com.fr.stable.ColumnRow;

import java.util.List;
import java.util.Map;

abstract class AbstractExcelReader {
    abstract List<Object[]> getRowDataList();
    abstract String[] getColumnNames();
    abstract int[] getColumnTypes();
    abstract Map<ColumnRow, ColumnRow> getMergeInfos();
}
