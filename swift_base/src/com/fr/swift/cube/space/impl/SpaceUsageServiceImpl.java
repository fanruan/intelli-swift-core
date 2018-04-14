package com.fr.swift.cube.space.impl;

import com.fr.swift.cube.space.SpaceUsage;
import com.fr.swift.cube.space.SpaceUsageDetector;
import com.fr.swift.cube.space.SpaceUsageService;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class SpaceUsageServiceImpl implements SpaceUsageService {
    private SpaceUsageDetector detector;
    private SwiftSegmentManager segmentManager;

    @Override
    public double getTableUsedSpace(SourceKey key) {
        List<Segment> segs = segmentManager.getSegment(key);

        double size = 0;
        for (Segment seg : segs) {
            size += detector.detect(seg.getLocation().getUri()).getUsed();
        }
        return size;
    }

    @Override
    public SpaceUsage getUsageOverall() {
        return null;
    }
}