package com.fr.swift.source.etl.detail;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by pony on 2018/1/12.
 */
public class DetailTransferOperator implements ETLTransferOperator {
    private List<ColumnKey[]> fields;

    public DetailTransferOperator(List<ColumnKey[]> fields) {
        this.fields = fields;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new DetailOperatorResultSet(metaData, fields, basedSegments);
    }
}
