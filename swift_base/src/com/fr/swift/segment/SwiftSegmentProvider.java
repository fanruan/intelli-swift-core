package com.fr.swift.segment;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/1/4
 */
public interface SwiftSegmentProvider extends SwiftSegmentManager {
    ISegmentOperator getIndexSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData);

    ISegmentOperator getIndexSegmentOperator(SourceKey sourceKey);

    ISegmentOperator getRealtimeSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData);

    ISegmentOperator getRealtimeSegmentOperator(SourceKey sourceKey);

    ISegmentOperator getDecreaseSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData);

    ISegmentOperator getDecreaseSegmentOperator(SourceKey sourceKey);


}
