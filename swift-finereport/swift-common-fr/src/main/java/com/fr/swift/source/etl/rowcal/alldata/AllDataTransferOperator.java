package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/2/24 0024 15:04
 */
public class AllDataTransferOperator implements ETLTransferOperator {
    private AggregatorType summaryType;
    private String columnName;
    private ColumnKey[] dimensions;

    public AllDataTransferOperator(AggregatorType summaryType, String columnName, ColumnKey[] dimensions) {
        this.summaryType = summaryType;
        this.columnName = columnName;
        this.dimensions = dimensions;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        AllDataCalculator cal = new AllDataCalculator(AggregatorFactory.createAggregator(summaryType));
        Segment[] segments = basedSegments.get(0);
        return new AllDataRowCalculatorResultSet(columnName, segments, metaData, cal, dimensions);
    }

}
