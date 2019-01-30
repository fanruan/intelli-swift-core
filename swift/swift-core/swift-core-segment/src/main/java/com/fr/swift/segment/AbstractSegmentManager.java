package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author pony
 * @date 2017/10/16
 */
public abstract class AbstractSegmentManager implements SwiftSegmentManager {
    protected SwiftSegmentService segmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
    protected SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
    protected SegmentContainer container;

    protected AbstractSegmentManager(SegmentContainer container) {
        this.container = container;
    }

    @Override
    public synchronized List<Segment> getSegment(SourceKey tableKey) {
        // 并发地拿，比如多个column indexer同时进行索引， 要同步下
        Integer currentFolder = getCurrentFolder(tablePathService, tableKey);
        if (container.contains(tableKey)) {
            return container.getSegments(tableKey, currentFolder);
        } else {
            List<SegmentKey> keys = getSegmentKeys(tableKey);
            return keys2Segments(keys, currentFolder);
        }
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
        List<Segment> segments = new ArrayList<Segment>();
        if (null == segmentIds || segmentIds.isEmpty()) {
            return getSegment(table);
        } else {
            Integer currentFolder = getCurrentFolder(tablePathService, table);
            List<SegmentKey> keys = new ArrayList<SegmentKey>();
            List<String> likeKeys = new ArrayList<String>();
            List<String> notLikeKeys = new ArrayList<String>();
            for (String segmentId : segmentIds) {
                if (segmentId.endsWith("-1")) {
                    String likeKey = segmentId.substring(0, segmentId.length() - 2);
                    if (!likeKeys.contains(likeKey)) {
                        keys.addAll(segmentService.find(
                                ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, table.getId()),
                                ConfigWhereImpl.like("id", likeKey, ConfigWhere.MatchMode.START)));
                        SwiftLoggers.getLogger().debug("RealTime like segments {}", keys);
                        likeKeys.add(likeKey);
                    }
                } else {
                    notLikeKeys.add(segmentId);
                }
            }

            List<SegmentKey> notMatchKeys = new ArrayList<SegmentKey>();
            if (!notLikeKeys.isEmpty()) {
                List<String> notMatch = new ArrayList<String>();
                segments.addAll(container.getSegments(notLikeKeys, notMatch, currentFolder));
                if (!notMatch.isEmpty()) {
                    notMatchKeys.addAll(segmentService.find(
                            ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, table.getId()),
                            ConfigWhereImpl.in("id", notMatch)));
                }
            }
            segments.addAll(container.getSegments(keys, notMatchKeys, currentFolder));
            if (!notMatchKeys.isEmpty()) {
                List<Segment> list = keys2Segments(notMatchKeys, currentFolder);
                segments.addAll(list);
            }
        }
        return segments;
    }

    @Override
    public void remove(SourceKey sourceKey) {
        container.remove(sourceKey);
    }
}