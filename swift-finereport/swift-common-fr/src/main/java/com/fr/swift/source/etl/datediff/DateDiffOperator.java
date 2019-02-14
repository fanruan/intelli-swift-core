package com.fr.swift.source.etl.datediff;

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
 * Created by Handsome on 2018/3/2 0002 14:04
 */
public class DateDiffOperator extends AbstractOperator {

    @CoreField
    private String field1;
    @CoreField
    private String field2;
    @CoreField
    private GroupType groupType;
    private String columnName;//新增列名

    public DateDiffOperator(String field1, String field2, GroupType groupType, String columnName) {
        this.field1 = field1;
        this.field2 = field2;
        this.groupType = groupType;
        this.columnName = columnName;
    }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumnBean(this.columnName, this.columnName,
                Types.BIGINT, ColumnTypeUtils.MAX_LONG_COLUMN_SIZE, 0, fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.DATE_DIFF;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(columnName);
    }

    public GroupType getGroupType() {
        return groupType;
    }
}
