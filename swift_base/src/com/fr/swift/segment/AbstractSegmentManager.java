package com.fr.swift.segment;

import com.fr.general.ComparatorUtils;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.IMetaDataColumn;
import com.fr.swift.config.ISegmentKey;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.cube.io.Types;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;

import java.net.URI;
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
//                SegmentXmlManager.getManager().getSegmentKeys(sourceKey);
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
        IConfigSegment segments = SegmentConfig.getInstance().getSegmentByKey(sourceKey);
        List<SegmentKey> target = new ArrayList<SegmentKey>();
        if (null != segments) {
            List<ISegmentKey> segmentKeys = segments.getSegments();

            for (int i = 0, len = segmentKeys.size(); i < len; i++) {
                ISegmentKey key = segmentKeys.get(i);
                Types.StoreType storeType = Types.StoreType.FINE_IO;
                String type = key.getStoreType();
                if (!ComparatorUtils.equalsIgnoreCase(Types.StoreType.FINE_IO.name(), type)) {
                    storeType = Types.StoreType.MEMORY;
                }
                SegmentKey segmentKey = new SegmentKey(key.getName(), URI.create(key.getUri()), key.getSegmentOrder(), storeType);
                segmentKey.setSourceId(key.getSourceId());
                target.add(segmentKey);
            }
        }
        return target;
    }
}
