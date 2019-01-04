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
 * Created by Handsome on 2018/2/22 0022 15:42
 */
public class GroupNumericOperator extends AbstractOperator {

    private String columnName;
    @CoreField
    private ColumnKey columnKey;
    @CoreField
    private double max;
    @CoreField
    private double min;
    @CoreField
    private String useOther;
    @CoreField
    private List<RestrictRange> nodes;

    public GroupNumericOperator(String columnName, ColumnKey columnKey, double max,
                                double min, String useOther, List<RestrictRange> nodes) {
        this.columnName = columnName;
        this.columnKey = columnKey;
        this.max = max;
        this.min = min;
        this.useOther = useOther;
        this.nodes = nodes;
    }

    public String getColumnName() {
        return columnName;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public String getUseOther() {
        return useOther;
    }

    public List<RestrictRange> getNodes() {
        return nodes;
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
        return OperatorType.GROUP_NUM;
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(columnName);
    }
}
