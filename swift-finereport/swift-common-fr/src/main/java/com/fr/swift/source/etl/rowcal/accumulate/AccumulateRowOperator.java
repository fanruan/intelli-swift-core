package com.fr.swift.source.etl.rowcal.accumulate;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 15:23
 */
public class AccumulateRowOperator extends AbstractOperator {
    @CoreField
    private ColumnKey columnKey;
    private String columnName;//新增列名
    @CoreField
    private ColumnKey[] dimension;

    public AccumulateRowOperator(ColumnKey columnKey, String columnName, ColumnKey[] dimension) {
        this.columnKey = columnKey;
        this.columnName = columnName;
        this.dimension = dimension;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public ColumnKey[] getDimension() {
        return dimension;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumnBean(this.columnName, this.columnName,
                Types.DOUBLE, fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(columnName);
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.ACCUMULATE;
    }

}
