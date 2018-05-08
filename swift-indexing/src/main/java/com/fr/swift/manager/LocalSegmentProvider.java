package com.fr.swift.manager;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * Created by pony on 2017/12/14.
 */
public class LocalSegmentProvider implements SwiftSegmentManager {
    private static LocalSegmentProvider ourInstance = new LocalSegmentProvider();

    public static LocalSegmentProvider getInstance() {
        return ourInstance;
    }

    private SwiftSegmentManager manager;

    private LocalSegmentProvider() {
        manager = new LineSegmentManager();
    }

    public void registerSwiftSegmentManager(SwiftSegmentManager manager) {
        this.manager = manager;
    }

    @Override
    public Segment getSegment(SegmentKey key) {
        return manager.getSegment(key);
    }

    @Override
    public List<Segment> getSegment(SourceKey sourceKey) {
        return manager.getSegment(sourceKey);
    }

    @Override
    public boolean isSegmentsExist(SourceKey key) {
        return manager.isSegmentsExist(key);
    }
}
