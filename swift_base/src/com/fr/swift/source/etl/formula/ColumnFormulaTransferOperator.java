package com.fr.swift.source.etl.formula;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/2/2 0002 10:00
 */
public class ColumnFormulaTransferOperator implements ETLTransferOperator {

    private int columnType;
    private String expression;

    public ColumnFormulaTransferOperator(int columnType, String expression) {
        this.columnType = columnType;
        this.expression = expression;
    }
    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new ColumnFormulaOperatorResultSet(this.columnType, this.expression, basedSegments.get(0));
    }
}
