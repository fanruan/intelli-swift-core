package com.fr.swift.source.etl.datediff;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 14:12
 */
public class DateDiffTransferOperator implements ETLTransferOperator {

    private String field1;
    private String field2;
    private GroupType groupType;

    public DateDiffTransferOperator(String field1, String field2, GroupType groupType) {
        this.field1 = field1;
        this.field2 = field2;
        this.groupType = groupType;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new DateDiffResultSet(field1, field2, groupType, basedSegments.get(0), metaData);
    }
}
