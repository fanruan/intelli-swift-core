package com.fr.swift.segment;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.third.org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Created by pony on 2017/10/16.
 */
public abstract class AbstractSegmentManager implements SwiftSegmentManager {
    protected SwiftSegmentService segmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
    protected SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);

    @Override
    public synchronized List<Segment> getSegment(SourceKey tableKey) {
        // 并发地拿，比如多个column indexer同时进行索引， 要同步下
        List<SegmentKey> keys = getSegmentKeys(tableKey);
        Integer currentFolder = getCurrentFolder(tablePathService, tableKey);
        return keys2Segments(keys, currentFolder);
    }

    private List<Segment> keys2Segments(List<SegmentKey> keys, Integer currentFolder) {
        List<Segment> segments = new ArrayList<Segment>();
        if (null != keys && !keys.isEmpty()) {
            for (SegmentKey key : keys) {
                try {
                    Segment segment = getSegment(key, currentFolder);
                    if (null != segment) {
                        segments.add(segment);
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }
        return Collections.unmodifiableList(segments);
    }

    @Override
    public boolean existsSegment(SegmentKey segKey) {
        return segmentService.containsSegment(segKey);
    }

    @Override
    public List<SegmentKey> getSegmentKeys(SourceKey tableKey) {
        return segmentService.getSegmentByKey(tableKey.getId());
    }

    @Override
    public Segment getSegment(SegmentKey key) {
        Integer currentFolder = getCurrentFolder(tablePathService, key.getTable());
        return getSegment(key, currentFolder);
    }

    @Override
    public boolean isSegmentsExist(SourceKey tableKey) {
        return !getSegmentKeys(tableKey).isEmpty();
    }

    protected abstract Integer getCurrentFolder(SwiftTablePathService service, SourceKey sourceKey);

    protected abstract Segment getSegment(SegmentKey segmentKey, Integer currentFolder);

    @Override
    public synchronized List<Segment> getSegmentsByIds(SourceKey table, Collection<String> segmentIds) {
        List<SegmentKey> keys;
        if (null == segmentIds || segmentIds.isEmpty()) {
            keys = segmentService.find(
                    Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, table.getId()));
        } else {
            keys = segmentService.find(
                    Restrictions.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, table.getId()),
                    Restrictions.in("id", segmentIds));
        }
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        Integer currentFolder = getCurrentFolder(tablePathService, table);
        return keys2Segments(keys, currentFolder);
    }
}