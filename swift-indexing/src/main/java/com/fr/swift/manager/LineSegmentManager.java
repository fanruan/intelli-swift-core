package com.fr.swift.manager;


import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.AbstractSegmentManager;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Util;

import java.net.URI;

/**
 * @author yee
 * @date 2017/12/18
 */
public class LineSegmentManager extends AbstractSegmentManager {

    @Override
    public Segment getSegment(SegmentKey segmentKey) {
        Util.requireNonNull(segmentKey);
        URI uri = segmentKey.getUri();
        Types.StoreType storeType = segmentKey.getStoreType();
        ResourceLocation location = new ResourceLocation(uri.getPath(), storeType);
        SourceKey sourceKey = segmentKey.getTable();
        SwiftMetaData metaData = MetaDataConvertUtil.getSwiftMetaDataBySourceKey(sourceKey.getId());
        Util.requireNonNull(metaData);
        switch (storeType) {
            case MEMORY:
                return new RealTimeSegmentImpl(location, metaData);
            case FINE_IO:
                return new HistorySegmentImpl(location, metaData);
            default:
                return new HistorySegmentImpl(location, metaData);
        }
    }

}
