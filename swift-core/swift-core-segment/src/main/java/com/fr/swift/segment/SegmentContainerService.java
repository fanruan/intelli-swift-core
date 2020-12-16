package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.source.SourceKey;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author lucifer
 * @date 2020/3/17
 * @description
 * @since swift 1.1
 */
@SwiftBean(name = "segmentService")
public class SegmentContainerService implements SegmentService {

    private SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);

    @Override
    public void addSegment(SegmentKey segmentKey) {
        SegmentContainer.LOCAL.addSegment(segmentKey);
        addElementByKeys(segmentKey == null ? Collections.EMPTY_LIST : Collections.singletonList(segmentKey));
    }

    @Override
    public SegmentKey transferSegment(SegmentKey oldSeg, SegmentKey newSeg) {
        SegmentKey transferedSeg = SegmentContainer.LOCAL.transferSegment(oldSeg, newSeg);
        addElementByKeys(newSeg == null ? Collections.EMPTY_LIST : Collections.singletonList(newSeg));
        deleteElementByKeys(oldSeg == null ? Collections.EMPTY_LIST : Collections.singletonList(oldSeg));
        return transferedSeg;
    }

    @Override
    public void addSegments(List<SegmentKey> segmentKeys) {
        SegmentContainer.LOCAL.addSegments(segmentKeys);
        addElementByKeys(segmentKeys);
    }

    @Override
    public Segment getSegment(SegmentKey key) {
        return SegmentContainer.LOCAL.getSegment(key);
    }

    @Override
    public List<Segment> getSegments(List<SegmentKey> keys) {
        return SegmentContainer.LOCAL.getSegments(keys);
    }

    @Override
    public List<Segment> getSegments(SourceKey tableKey) {
        return SegmentContainer.LOCAL.getSegments(tableKey);
    }

    @Override
    public List<Segment> getSegments(Set<String> segKeys) {
        return SegmentContainer.LOCAL.getSegments(segKeys);
    }

    @Override
    public List<SegmentKey> getSegmentKeys(SourceKey tableKey) {
        return SegmentContainer.LOCAL.getSegmentKeys(tableKey);
    }

    @Override
    public List<SegmentKey> getSegmentKeysByIds(SourceKey tableKey, Collection<String> segmentIds) {
        return SegmentContainer.LOCAL.getSegmentKeysByIds(tableKey, segmentIds);
    }

    @Override
    public boolean exist(SegmentKey segmentKey) {
        return SegmentContainer.LOCAL.exist(segmentKey);
    }

    @Override
    public boolean existAll(Collection<String> segmentIds) {
        return SegmentContainer.LOCAL.existAll(segmentIds);
    }

    @Override
    public SegmentKey removeSegment(SegmentKey segmentKey) {
        SegmentKey removedSegkey = SegmentContainer.LOCAL.removeSegment(segmentKey);
        deleteElementByKeys(removedSegkey == null ? Collections.EMPTY_LIST : Collections.singletonList(removedSegkey));
        return removedSegkey;
    }

    @Override
    public List<SegmentKey> removeSegments(List<SegmentKey> segmentKeys) {
        List<SegmentKey> removedSegkeys = SegmentContainer.LOCAL.removeSegments(segmentKeys);
        deleteElementByKeys(removedSegkeys);
        return removedSegkeys;
    }

    @Override
    public SwiftSegmentBucket getBucketByTable(SourceKey sourceKey) {
        return SegmentContainer.LOCAL.getBucketByTable(sourceKey);
    }

    @Override
    public <S extends SegmentIndex> SegmentIndex computeSegIndexIfAbsent(SourceKey sourceKey, Function<SourceKey, S> function) {
        return SegmentContainer.LOCAL.computeSegIndexIfAbsent(sourceKey, function);
    }

    @Override
    public void addSegmentIndex(SourceKey sourceKey, SegmentIndex segmentIndex) {
        SegmentContainer.LOCAL.addSegmentIndex(sourceKey, segmentIndex);
    }

    @Override
    public void flushCache() {
        SegmentContainer.LOCAL.flushCache();
    }

    private void addElementByKeys(Collection<SegmentKey> keys) {
        List<SwiftSegmentBucketElement> elements = swiftSegmentService.getBucketElementsByKeys(keys);
        for (SwiftSegmentBucketElement element : elements) {
            SegmentContainer.LOCAL.saveBucket(element);
        }
    }

    private void deleteElementByKeys(Collection<SegmentKey> keys) {
        for (SegmentKey key : keys) {
            SegmentContainer.LOCAL.deleteBucket(key);
        }
    }
}
