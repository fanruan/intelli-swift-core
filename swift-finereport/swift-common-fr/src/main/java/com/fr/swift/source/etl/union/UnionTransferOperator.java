package com.fr.swift.source.etl.union;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 10:52
 */
public class UnionTransferOperator implements ETLTransferOperator {

    private List<List<String>> unionColumns;

    public UnionTransferOperator(List<List<String>> unionColumns) {
        this.unionColumns = unionColumns;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        List<List<Segment>> segments = new ArrayList<List<Segment>>();
        for (int i = 0; i < basedSegments.size(); i++) {
            segments.add(Arrays.asList(basedSegments.get(i)));
        }
        return new UnionOperatorResultSet(unionColumns, segments, metaData);
    }
}
