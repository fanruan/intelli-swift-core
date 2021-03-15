package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * @author anchore
 * @date 2018/10/30
 */
@SwiftBean(name = "backupSegment")
@SwiftScope("prototype")
public class BackupSegment extends CacheColumnSegment {

    public BackupSegment(IResourceLocation location, SwiftMetaData meta) {
        super(location, meta);
    }
}