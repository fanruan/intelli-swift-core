package com.fr.swift.source.etl.rowcal.accumulate;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 15:30
 */
public class AccumulateRowTransferOperator implements ETLTransferOperator {

    private ColumnKey columnKey;
    private ColumnKey[] dimensions;

    public AccumulateRowTransferOperator(ColumnKey columnKey, ColumnKey[] dimensions) {
        this.columnKey = columnKey;
        this.dimensions = dimensions;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new AccumulateRowResultSet(columnKey, basedSegments.get(0), metaData, dimensions);
    }
}
