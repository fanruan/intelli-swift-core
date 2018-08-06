package com.fr.swift.manager;


import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.AbstractSegmentManager;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
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
    protected Integer getCurrentFolder(SwiftTablePathService service, SourceKey sourceKey) {
        return service.getTablePath(sourceKey.getId());
    }

    @Override
    public Segment getSegment(SegmentKey segmentKey, Integer currentFolder) {
        Util.requireNonNull(segmentKey);
        URI uri = segmentKey.getUri();
        String cubePath = String.format("%s/%d/%s",
                segmentKey.getSwiftSchema().getDir(),
                currentFolder, uri.getPath());
        Types.StoreType storeType = segmentKey.getStoreType();
        ResourceLocation location = new ResourceLocation(cubePath, storeType);
        SourceKey sourceKey = segmentKey.getTable();
        SwiftMetaData metaData = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(sourceKey.getId());
        Util.requireNonNull(metaData);
        switch (storeType) {
            case MEMORY:
                return SegmentUtils.newRealtimeSegment(location, metaData);
            case FINE_IO:
                return SegmentUtils.newHistorySegment(location, metaData);
            default:
                return SegmentUtils.newHistorySegment(location, metaData);
        }
    }

}
