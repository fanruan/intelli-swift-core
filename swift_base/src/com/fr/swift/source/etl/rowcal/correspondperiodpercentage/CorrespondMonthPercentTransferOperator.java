package com.fr.swift.source.etl.rowcal.correspondperiodpercentage;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/3/4 0006 14:05
 */
public class CorrespondMonthPercentTransferOperator implements ETLTransferOperator {

    private ColumnKey columnKey;
    private ColumnKey periodKey;
    private ColumnKey[] dimensions;

    public CorrespondMonthPercentTransferOperator(ColumnKey columnKey, ColumnKey periodKey, ColumnKey[] dimensions) {
        this.columnKey = columnKey;
        this.periodKey = periodKey;
        this.dimensions = dimensions;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new CorrespondMonthPercentResultSet(columnKey, periodKey, dimensions, basedSegments.get(0), metaData);
    }
}
