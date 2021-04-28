package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * @author lucifer
 * @date 2019-07-30
 * @description
 * @since advanced swift 1.0
 */
public class MutableCacheColumnSegment extends CacheColumnSegment {

    public MutableCacheColumnSegment(IResourceLocation location, SwiftMetaData meta) {
        super(location, meta);
    }

    public void refreshMetadata(SwiftMetaData metaData) {
        super.meta = metaData;
    }
}
