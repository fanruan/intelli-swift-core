package com.fr.swift.source.etl.date;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 17:31
 */
public class GetFromDateTransferOperator implements ETLTransferOperator {

    private String field;
    private GroupType type;

    public GetFromDateTransferOperator(String field, GroupType type) {
        this.field = field;
        this.type = type;
    }


    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new GetFromDateResultSet(field, type, basedSegments.get(0), metaData);
    }
}
