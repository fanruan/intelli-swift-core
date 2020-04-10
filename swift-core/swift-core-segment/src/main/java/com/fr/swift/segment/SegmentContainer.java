package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yee
 * @date 2018/10/19
 */
public enum SegmentContainer implements SegmentService {
    /**
     * instance
     */
    LOCAL;
    private SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
    private SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);

    private final Map<SourceKey, Set<SegmentKey>> tableMap = new ConcurrentHashMap<>();
    private final Map<String, SegmentKey> segmentKeyMap = new ConcurrentHashMap<>();
    private final Map<SourceKey, SwiftSegmentBucket> bucketMap = new ConcurrentHashMap<>();

    SegmentContainer() {
        List<SwiftMetaData> allMetas = metaDataService.getAllMetas();
        for (SwiftMetaData meta : allMetas) {
            try {
                List<SegmentKey> segmentKeyList = swiftSegmentService.getOwnSegments(new SourceKey(meta.getTableName()));
                addSegments(segmentKeyList);
                SourceKey tableKey = new SourceKey(meta.getTableName());
                SwiftSegmentBucket bucket = swiftSegmentService.getBucketByTable(tableKey);
                bucketMap.put(tableKey, bucket);
            } catch (SwiftMetaDataException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    @Override
    public void addSegment(SegmentKey segmentKey) {
        segmentKeyMap.put(segmentKey.getId(), segmentKey);
        tableMap.computeIfAbsent(segmentKey.getTable(), n -> new HashSet<>()).add(segmentKey);
    }

    @Override
    public void addSegments(List<SegmentKey> segmentKeys) {
        for (SegmentKey segmentKey : segmentKeys) {
            addSegment(segmentKey);
        }
    }

    @Override
    public Segment getSegment(SegmentKey key) {
        return SegmentUtils.newSegment(key);
    }

    @Override
    public List<Segment> getSegments(List<SegmentKey> keys) {
        return keys.stream().map(this::getSegment).collect(Collectors.toList());
    }


    @Override
    public List<Segment> getSegments(SourceKey tableKey) {
        Set<SegmentKey> segmentKeys = tableMap.computeIfAbsent(tableKey, n -> new HashSet<>());
        return segmentKeys.stream()
                .map(this::getSegment)
                .collect(Collectors.toList());
    }

    @Override
    public List<Segment> getSegments(Set<String> segKeys) {
        return segKeys.stream()
                .filter(s -> segmentKeyMap.containsKey(s))
                .map(s -> SegmentUtils.newSegment(segmentKeyMap.get(s)))
                .collect(Collectors.toList());
    }

    @Override
    public List<SegmentKey> getSegmentKeys(SourceKey tableKey) {
        return tableMap.computeIfAbsent(tableKey, n -> new HashSet<>()).stream().collect(Collectors.toList());
    }

    @Override
    public List<SegmentKey> getSegmentKeysByIds(SourceKey tableKey, Collection<String> segmentIds) {
        if (null == segmentIds || segmentIds.isEmpty()) {
            return getSegmentKeys(tableKey);
        }
        return tableMap.computeIfAbsent(tableKey, n -> new HashSet<>())
                .stream()
                .filter(s -> segmentIds.contains(s.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean exist(SegmentKey segmentKey) {
        return segmentKeyMap.containsKey(segmentKey.getId());
    }

    @Override
    public SegmentKey removeSegment(SegmentKey segmentKey) {
        SegmentKey removedSegmentKey = segmentKeyMap.remove(segmentKey.getId());
        tableMap.getOrDefault(segmentKey.getTable(), new HashSet<>()).remove(removedSegmentKey);
        return removedSegmentKey;
    }

    @Override
    public List<SegmentKey> removeSegments(List<SegmentKey> segmentKeys) {
        return segmentKeys.stream().map(this::removeSegment).filter(s -> s != null).collect(Collectors.toList());
    }

    @Override
    public SwiftSegmentBucket getBucketByTable(SourceKey sourceKey) {
        return bucketMap.computeIfAbsent(sourceKey, n -> new SwiftSegmentBucket(sourceKey));
    }

    void saveBucket(SwiftSegmentBucketElement element) {
        SourceKey sourceKey = new SourceKey(element.getSourceKey());
        bucketMap.computeIfAbsent(sourceKey, n -> new SwiftSegmentBucket(sourceKey))
                .put(element.getBucketIndex(), segmentKeyMap.get(element.getRealSegmentKey()));
    }

    void deleteBucket(SegmentKey segmentKey) {
        bucketMap.computeIfAbsent(segmentKey.getTable(), n -> new SwiftSegmentBucket(segmentKey.getTable())).remove(segmentKey);
    }
}