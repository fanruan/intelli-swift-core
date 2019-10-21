package com.fr.swift.segment;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author anchore
 * @date 2018/10/30
 */
public class BackupSegment extends CacheColumnSegment {

    public BackupSegment(IResourceLocation location, SwiftMetaData meta) {
        super(location, meta);
    }
}