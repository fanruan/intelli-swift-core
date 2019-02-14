package com.fr.swift.source.etl.rowcal.rank;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeUtils;
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
 * Created by Handsome on 2018/2/28 0028 10:55
 */
public class RankRowOperator extends AbstractOperator {
    @CoreField
    private SortType sortType;
    @CoreField
    private ColumnKey columnKey;
    private String columnName;//新增列
    @CoreField
    private ColumnKey[] dimension;

    public RankRowOperator(String columnName, SortType type, ColumnKey columnKey, ColumnKey[] dimension) {
        this.sortType = type;
        this.columnName = columnName;
        this.columnKey = columnKey;
        this.dimension = dimension;
    }

    public ColumnKey[] getDimension() {
        return dimension;
    }

    public String getColumnName() {
        return columnName;
    }

    public SortType getType() {
        return sortType;
    }


    public ColumnKey getColumnKey() {
        return columnKey;
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(columnName);
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumnBean(this.columnName, this.columnName,
                Types.BIGINT, ColumnTypeUtils.MAX_LONG_COLUMN_SIZE - 1, 0, fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.RANK;
    }

}
