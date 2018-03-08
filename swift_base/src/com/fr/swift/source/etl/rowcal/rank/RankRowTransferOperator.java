package com.fr.swift.source.etl.rowcal.rank;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 11:03
 */
public class RankRowTransferOperator implements ETLTransferOperator {

    private int type;
    private ColumnKey columnKey;
    private ColumnKey[] dimension;

    public RankRowTransferOperator(int type, ColumnKey columnKey, ColumnKey[] dimension) {
        this.type = type;
        this.columnKey = columnKey;
        this.dimension = dimension;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new RankRowResultSet(type, columnKey, basedSegments.get(0), metaData, dimension);
    }
}
