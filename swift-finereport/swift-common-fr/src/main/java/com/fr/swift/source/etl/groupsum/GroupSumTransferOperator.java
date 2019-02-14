package com.fr.swift.source.etl.groupsum;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 12:01
 */
public class GroupSumTransferOperator implements ETLTransferOperator {

    private GroupSumTarget[] targets;
    private GroupSumDimension[] dimensions;

    public GroupSumTransferOperator(GroupSumTarget[] targets, GroupSumDimension[] dimensions) {
        this.targets = targets;
        this.dimensions = dimensions;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new GroupSumOperatorResultSet(this.targets, this.dimensions, basedSegments.get(0), metaData);
    }
}
