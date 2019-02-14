package com.fr.swift.source.etl.group;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/2/22 0022 14:12
 */
public class GroupAssignmentTransferOperator implements ETLTransferOperator {

    private String otherName;
    private ColumnKey columnKey;
    private List<SingleGroup> group;

    public GroupAssignmentTransferOperator(String otherName, ColumnKey columnKey, List<SingleGroup> group) {
        this.otherName = otherName;
        this.columnKey = columnKey;
        this.group = group;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new GroupAssignmentResultSet(otherName, columnKey, group, metaData, basedSegments.get(0));
    }
}
