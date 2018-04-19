package com.fr.swift.segment.operator.insert;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeBlockSwiftInserter extends AbstractBlockInserter {

    public RealtimeBlockSwiftInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData) {
        super(segments, sourceKey, cubeSourceKey, swiftMetaData);
    }

    public RealtimeBlockSwiftInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData, List<String> fields) {
        super(segments, sourceKey, cubeSourceKey, swiftMetaData, fields);
    }

    @Override
    protected Segment createNewSegment(IResourceLocation location, SwiftMetaData swiftMetaData) {
        return new RealTimeSegmentImpl(location, swiftMetaData);
    }

    @Override
    protected Segment createSegment(int order) {
        return createSegment(order, Types.StoreType.MEMORY);
    }
}
