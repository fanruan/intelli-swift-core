package com.fr.swift.source.etl;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * Created by pony on 2018/1/4.
 */
public interface ETLTransferOperator {
    SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments);
}
