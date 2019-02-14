package com.fr.swift.source.etl.columnfilter;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/1/24 0024 15:31
 */
public class ColumnFilterTransferOperator implements ETLTransferOperator {

    private FilterInfo filterInfo;

    public ColumnFilterTransferOperator(FilterInfo filterInfo) {
        this.filterInfo = filterInfo;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new ColumnFilterOperatorResultSet(basedSegments.get(0), basedMetas.get(0), metaData, filterInfo);
    }
}
