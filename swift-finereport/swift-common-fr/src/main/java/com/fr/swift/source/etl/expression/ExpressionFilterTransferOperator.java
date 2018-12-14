package com.fr.swift.source.etl.expression;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/3/6 0013 11:51
 */
public class ExpressionFilterTransferOperator implements ETLTransferOperator {

    private ColumnKey columnKey;
    private int type;
    private FilterInfo[] filters;
    private Object[] values;
    private Object otherValue;

    public ExpressionFilterTransferOperator(ColumnKey columnKey, int type, FilterInfo[] filters,
                                            Object[] values, Object otherValue) {
        this.columnKey = columnKey;
        this.type = type;
        this.filters = filters;
        this.values = values;
        this.otherValue = otherValue;
    }


    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new ExpressionFilterResultSet(columnKey, type, filters, values, otherValue, basedSegments.get(0), metaData);
    }
}
