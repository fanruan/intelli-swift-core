package com.fr.swift.source.etl.datediff;

import com.fr.swift.source.*;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 14:04
 */
public class DateDiffOperator extends AbstractOperator {

    @CoreField
    private String field1;
    @CoreField
    private String field2;
    @CoreField
    private int unit;
    @CoreField
    private String columnName;//新增列名
    @CoreField
    private int columnType;

    public DateDiffOperator(String field1, String field2, int unit, String columnName, int columnType) {
        this.field1 = field1;
        this.field2 = field2;
        this.unit = unit;
        this.columnName = columnName;
        this.columnType = columnType;
    }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(this.columnName, this.columnName,
                this.columnType, MD5Utils.getMD5String(new String[]{(this.columnName)})));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.DATEDIFF;
    }

    public String getColumnMD5() {
        return MD5Utils.getMD5String(new String[]{(this.columnName)});
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public int getUnit() {
        return unit;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public String getNewAddedName() {
        return columnName;
    }

}
