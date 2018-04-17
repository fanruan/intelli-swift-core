package com.fr.swift.cube.space.impl;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.space.SpaceUsageDetector;
import com.fr.swift.cube.space.SpaceUsageService;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class SpaceUsageServiceImpl implements SpaceUsageService {
    private SpaceUsageDetector detector = new LocalSpaceUsageDetector();

    private SwiftSegmentManager segmentManager = SwiftContext.getInstance().getSegmentProvider();

    @Override
    public long getTableUsedSpace(SourceKey table) throws Exception {
        List<Segment> segs = segmentManager.getSegment(table);

        long size = 0;
        for (Segment seg : segs) {
            size += detector.detectUsed(seg.getLocation().getUri());
        }
        return size;
    }

    @Override
    public long getTableUsedSpace(List<SourceKey> tables) throws Exception {
        long used = 0;
        for (SourceKey table : tables) {
            used += getTableUsedSpace(table);
        }
        return used;
    }

    @Override
    public long getUsedOverall() throws Exception {
        URI baseUri = null;
        return detector.detectUsed(baseUri);
    }

    @Override
    public long getUsableOverall() throws Exception {
        URI baseUri = null;
        return detector.detectUsable(baseUri);
    }

    @Override
    public long getTotalOverall() throws Exception {
        URI baseUri = null;
        return detector.detectTotal(baseUri);
    }

    private static final SpaceUsageService INSTANCE = new SpaceUsageServiceImpl();

    private SpaceUsageServiceImpl() {
    }

    public static SpaceUsageService getInstance() {
        return INSTANCE;
    }
}