package com.fr.swift.source.excel.data;

import com.fr.general.data.DataModel;
import com.fr.swift.source.ColumnTypeConstants;

/**
 * @author yee
 * @date 2018/4/26
 */
public interface IExcelDataModel extends DataModel {
    String[] onlyGetColumnNames();

    ColumnTypeConstants.ColumnType[] onlyGetColumnTypes();

    ColumnTypeConstants.ColumnType getColumnType(int i);
}
