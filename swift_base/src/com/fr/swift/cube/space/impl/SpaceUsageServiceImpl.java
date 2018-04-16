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
    public double getTableUsedSpace(SourceKey table) throws Exception {
        List<Segment> segs = segmentManager.getSegment(table);

        double size = 0;
        for (Segment seg : segs) {
            size += detector.detect(seg.getLocation().getUri()).getUsed();
        }
        return size;
    }

    @Override
    public double getTableUsedSpace(List<SourceKey> tables) throws Exception {
        double used = 0;
        for (SourceKey table : tables) {
            used += getTableUsedSpace(table);
        }
        return used;
    }

    @Override
    public SpaceUsage getUsageOverall() {
        return null;
    }
}