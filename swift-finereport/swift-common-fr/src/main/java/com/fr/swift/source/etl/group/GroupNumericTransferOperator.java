package com.fr.swift.source.etl.group;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/2/23 0023 09:52
 */
public class GroupNumericTransferOperator implements ETLTransferOperator {

    private ColumnKey columnKey;
    private double max;
    private double min;
    private String useOther;
    private List<RestrictRange> nodes;

    public GroupNumericTransferOperator(ColumnKey columnKey, double max, double min,
                                        String useOther, List<RestrictRange> nodes) {
        this.columnKey = columnKey;
        this.max = max;
        this.min = min;
        this.useOther = useOther;
        this.nodes = nodes;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new GroupNumericResultSet(columnKey, max, min, useOther, nodes, basedSegments.get(0), metaData);
    }
}
