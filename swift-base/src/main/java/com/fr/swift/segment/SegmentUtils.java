package com.fr.swift.segment;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * This class created on 2018/7/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SegmentUtils {
    public static Segment newRealtimeSegment(IResourceLocation location, SwiftMetaData meta) {
        return (Segment) SwiftContext.get().getBean("realtimeSegment", location, meta);
    }

    public static Segment newHistorySegment(IResourceLocation location, SwiftMetaData meta) {
        return (Segment) SwiftContext.get().getBean("historySegment", location, meta);
    }

    public static SegmentKey getMaxSegmentKey(List<SegmentKey> segmentKeys) {
        if (segmentKeys.isEmpty()) {
            return null;
        } else {
            SegmentKey maxSegmentKey = segmentKeys.get(0);
            for (SegmentKey segmentKey : segmentKeys) {
                if (segmentKey.getOrder() > maxSegmentKey.getOrder()) {
                    maxSegmentKey = segmentKey;
                }
            }
            return maxSegmentKey;
        }
    }
}
