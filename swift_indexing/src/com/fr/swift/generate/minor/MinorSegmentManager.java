package com.fr.swift.generate.minor;

import com.fr.swift.segment.IMinorSegmentManager;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/2/2
 */
public class MinorSegmentManager implements IMinorSegmentManager {
    private Map<SourceKey, List<Segment>> segments = new ConcurrentHashMap<SourceKey, List<Segment>>();

    @Override
    public Segment getSegment(SegmentKey key) {
        return null;
    }

    @Override
    public List<Segment> getSegment(SourceKey sourceKey) {
        List<Segment> segmentList = segments.get(sourceKey);
        return segmentList == null ? new ArrayList<Segment>() : segmentList;
    }

    @Override
    public void update(DataSource dataSource) throws Exception {
        MinorUpdater.update(dataSource);
    }

    public void putSegment(SourceKey key, List<Segment> seg) {
        Util.requireNonNull(key, seg);
        segments.put(key, seg);
    }

    public void clear() {
        segments.clear();
    }

    private static final MinorSegmentManager INSTANCE = new MinorSegmentManager();

    private MinorSegmentManager() {
    }

    public static MinorSegmentManager getInstance() {
        return INSTANCE;
    }

}