package com.fr.swift.generate.segment.operator.inserter;

import com.fr.general.ComparatorUtils;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.insert.AbstractBlockInserter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * This class created on 2018/4/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class BlockInserter extends AbstractBlockInserter {

    public BlockInserter(SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData) {
        super(sourceKey, cubeSourceKey, swiftMetaData);
    }

    public BlockInserter(SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData, List<String> fields) {
        super(sourceKey, cubeSourceKey, swiftMetaData, fields);
    }

    public BlockInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData) {
        super(segments, sourceKey, cubeSourceKey, swiftMetaData);
    }

    public BlockInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData, List<String> fields) {
        super(segments, sourceKey, cubeSourceKey, swiftMetaData, fields);
    }

    @Override
    protected Segment createSegment(int order) {
        if (!ComparatorUtils.equals(sourceKey.getId(), cubeSourceKey)) {
            List<Segment> cubeSourceSegments = SwiftContext.get().getBean(SwiftSegmentManager.class).getSegment(new SourceKey(cubeSourceKey));
            Segment segment = cubeSourceSegments.get(order);
            return createSegment(order, segment.isHistory() ? Types.StoreType.FINE_IO : Types.StoreType.MEMORY);
        }
        return createSegment(order, Types.StoreType.FINE_IO);
    }

    @Override
    protected Segment createNewSegment(IResourceLocation location, SwiftMetaData swiftMetaData) {
        if (location.getStoreType() == Types.StoreType.FINE_IO) {
            return new HistorySegmentImpl(location, swiftMetaData);
        } else {
            return new RealTimeSegmentImpl(location, swiftMetaData);
        }
    }
}
