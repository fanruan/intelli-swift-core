package com.fr.swift.segment;

import com.fr.swift.config.conf.bean.SegmentBean;
import com.fr.swift.config.conf.service.SwiftConfigServiceProvider;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pony on 2017/10/16.
 */
public abstract class AbstractSegmentManager implements SwiftSegmentManager {
    @Override
    public synchronized List<Segment> getSegment(SourceKey sourceKey) {
        // 并发地拿，比如多个column indexer同时进行索引， 要同步下
        List<Segment> segments = new ArrayList<Segment>();
        List<SegmentKey> keys = getSegmentKey(sourceKey.getId());
        if (null != keys && !keys.isEmpty()) {
            for (SegmentKey key : keys) {
                try {
                    Segment segment = getSegment(key);
                    if (null != segment) {
                        segments.add(segment);
                    }
                } catch (Exception e) {
                    return segments;
                }
            }
        }
        return segments;
    }

    @Override
    public boolean isSegmentsExist(SourceKey sourceKey) {
        return !getSegmentKey(sourceKey.getId()).isEmpty();
    }

    private List<SegmentKey> getSegmentKey(String sourceKey) {
        List<SegmentKey> segments = SwiftConfigServiceProvider.getInstance().getSegmentByKey(sourceKey);
        List<SegmentKey> target = new ArrayList<SegmentKey>();
        if (null != segments) {
            for (SegmentKey key : segments) {
                target.add(new SwiftSegmentKey(key));
            }
        }
        return target;
    }
}