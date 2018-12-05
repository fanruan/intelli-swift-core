package com.fr.swift.generate.preview;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/2/2
 */
public class MinorSegmentManager implements SwiftSegmentManager {
    private Map<SourceKey, List<Segment>> segments = new ConcurrentHashMap<SourceKey, List<Segment>>();

    @Override
    public Segment getSegment(SegmentKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsSegment(SegmentKey segKey) {
        return false;
    }

    @Override
    public List<Segment> getSegment(SourceKey tableKey) {
        if (isSegmentsExist(tableKey)) {
            return segments.get(tableKey);
        }
        return Collections.emptyList();
    }

    @Override
    public List<SegmentKey> getSegmentKeys(SourceKey tableKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSegmentsExist(SourceKey tableKey) {
        return segments.containsKey(tableKey);
    }

    @Override
    public List<Segment> getSegmentsByIds(SourceKey table, Collection<String> segmentIds) {
        throw new UnsupportedOperationException();
    }

    public void putSegment(SourceKey key, List<Segment> seg) {
        Util.requireNonNull(key, seg);
        segments.put(key, seg);
    }

    public void clear() {
        segments.clear();
    }

    @Override
    public List<Segment> remove(SourceKey sourceKey) {
        return segments.remove(sourceKey);
    }

    private static final MinorSegmentManager INSTANCE = new MinorSegmentManager();

    private MinorSegmentManager() {
    }

    public static MinorSegmentManager getInstance() {
        return INSTANCE;
    }
}