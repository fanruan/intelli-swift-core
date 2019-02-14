package com.fr.swift.source.etl.group;

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
 * Created by Handsome on 2018/2/22 0022 11:30
 */
public class GroupAssignmentOperator extends AbstractOperator {
    private String columnName;
    @CoreField
    private String otherName;
    @CoreField
    private ColumnKey columnKey;
    @CoreField
    private List<SingleGroup> group;

    public GroupAssignmentOperator(String columnName, String otherName, ColumnKey columnKey, List<SingleGroup> group) {
        this.columnName = columnName;
        this.otherName = otherName;
        this.columnKey = columnKey;
        this.group = group;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumnBean(this.columnName,
                this.columnName, Types.VARCHAR, fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.GROUP_STRING;
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(columnName);
    }

    public String getOtherName() {
        return otherName;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public List<SingleGroup> getGroup() {
        return group;
    }
}
