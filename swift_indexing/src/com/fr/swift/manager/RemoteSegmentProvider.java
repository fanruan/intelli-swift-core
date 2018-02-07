package com.fr.swift.manager;

import com.fr.swift.segment.ISegmentOperator;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentProvider;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * @author yee
 * @date 2017/12/19
 */
public class RemoteSegmentProvider implements SwiftSegmentProvider {

    private static RemoteSegmentProvider ourInstance = new RemoteSegmentProvider();

    public static RemoteSegmentProvider getInstance() {
        return ourInstance;
    }

    @Override
    public Segment getSegment(SegmentKey key) {
        return null;
    }

    @Override
    public List<Segment> getSegment(SourceKey sourceKey) {
        return null;
    }

    @Override
    public boolean isSegmentsExist(SourceKey key) {
        return false;
    }

    @Override
    public ISegmentOperator getIndexSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData) {
        return null;
    }

    @Override
    public ISegmentOperator getIndexSegmentOperator(SourceKey sourceKey) {
        return null;
    }

    @Override
    public ISegmentOperator getRealtimeSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData) {
        return null;
    }

    @Override
    public ISegmentOperator getRealtimeSegmentOperator(SourceKey sourceKey) {
        return null;
    }

    @Override
    public ISegmentOperator getDecreaseSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData) {
        return null;
    }

    @Override
    public ISegmentOperator getDecreaseSegmentOperator(SourceKey sourceKey) {
        return null;
    }
}
