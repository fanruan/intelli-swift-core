package com.fr.swift.segment.operator.utils;

import com.fr.swift.segment.SegmentKey;

import java.util.List;

/**
 * This class created on 2018/7/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SegmentUtils {
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
