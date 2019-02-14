package com.fr.swift.source.etl.date;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.query.group.GroupType;
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
 * Created by Handsome on 2018/3/2 0002 17:22
 */
public class GetFromDateOperator extends AbstractOperator {

    @CoreField
    private String field;
    @CoreField
    private GroupType type;
    private String columnName;//新增列名

    public GetFromDateOperator(String field, GroupType type, String columnName) {
        this.field = field;
        this.type = type;
        this.columnName = columnName;
    }

    public String getField() {
        return field;
    }

    public GroupType getType() {
        return type;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(columnName);
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumnBean(this.columnName, this.columnName,
                getColumnType(), ColumnTypeUtils.MAX_LONG_COLUMN_SIZE, 0, fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.GET_DATE;
    }

    public int getColumnType() {
        switch (type) {
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
            case WEEK_OF_YEAR:
                return Types.BIGINT;
            default:
                return Types.DATE;
        }
    }
}
