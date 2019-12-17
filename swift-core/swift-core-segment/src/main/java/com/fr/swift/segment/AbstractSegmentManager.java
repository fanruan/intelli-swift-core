package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.oper.ConfigWhere.MatchMode;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pony
 * @date 2017/10/16
 */
public abstract class AbstractSegmentManager implements SwiftSegmentManager {
    private final SwiftSegmentLocationService segLocationSvc = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
    protected SwiftSegmentService segmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
    protected SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
    protected SegmentContainer container;

    protected AbstractSegmentManager(SegmentContainer container) {
        this.container = container;
    }

    @Override
    public List<Segment> getSegment(SourceKey tableKey) {
        synchronized (this) {
            // 并发地拿，比如多个column indexer同时进行索引， 要同步下
            Integer currentFolder = getCurrentFolder(tablePathService, tableKey);
            if (container.contains(tableKey)) {
                return container.getSegments(tableKey, currentFolder);
            } else {
                List<SegmentKey> keys = getSegmentKeys(tableKey);
                return keys2Segments(keys, currentFolder);
            }
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
        // TODO: 2019-08-03 anchore 直接查location可以吗
        return segLocationSvc.containsLocal(segKey) &&
                segmentService.containsSegment(segKey);
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
            //table@MEMORY@0
            List<SegmentKey> memSegKeys = new ArrayList<SegmentKey>();
            //table@MEMORY
            List<String> memSegKeyHeads = new ArrayList<String>();
            //table@FINE_IO@1
            List<String> hisSegKeys = new ArrayList<String>();
            for (String segmentId : segmentIds) {
                if (segmentId.endsWith("-1")) {
                    String memSegKeyHead = segmentId.substring(0, segmentId.length() - 2);
                    if (!memSegKeyHeads.contains(memSegKeyHead)) {
                        List<SwiftSegmentLocationEntity> localSegLocations = segLocationSvc.find(
                                ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()),
                                ConfigWhereImpl.eq("sourceKey", table.getId()),
                                ConfigWhereImpl.like("id.segmentId", memSegKeyHead, MatchMode.START));
                        Set<String> segIds = new HashSet<>();
                        for (SwiftSegmentLocationEntity localSegLocation : localSegLocations) {
                            segIds.add(localSegLocation.getSegmentId());
                        }
                        memSegKeys.addAll(segmentService.getByIds(segIds));
                        SwiftLoggers.getLogger().debug("RealTime like segments {}", memSegKeys);
                        memSegKeyHeads.add(memSegKeyHead);
                    }
                } else {
                    hisSegKeys.add(segmentId);
                }
            }

            List<SegmentKey> notMatchKeys = new ArrayList<SegmentKey>();
            if (!hisSegKeys.isEmpty()) {
                List<String> notMatch = new ArrayList<String>();
                segments.addAll(container.getSegments(hisSegKeys, notMatch, currentFolder));
                if (!notMatch.isEmpty()) {
                    List<SwiftSegmentLocationEntity> localSegLocations = segLocationSvc.find(
                            ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()),
                            ConfigWhereImpl.eq("sourceKey", table.getId()),
                            ConfigWhereImpl.in("id.segmentId", notMatch));
                    Set<String> notMatchSegIds = new HashSet<>();
                    for (SwiftSegmentLocationEntity localSegLocation : localSegLocations) {
                        notMatchSegIds.add(localSegLocation.getSegmentId());
                    }
                    notMatchKeys.addAll(segmentService.getByIds(notMatchSegIds));
                    container.register(notMatchKeys);
                }
            }
            segments.addAll(container.getSegments(memSegKeys, notMatchKeys, currentFolder));
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