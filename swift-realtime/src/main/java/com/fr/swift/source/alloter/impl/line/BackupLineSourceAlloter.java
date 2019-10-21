package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.BackupSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author anchore
 * @date 2019/3/12
 * <p>
 * 和{@link RealtimeLineSourceAlloter}分块规则相同，区别只在读备份块
 */
public class BackupLineSourceAlloter extends RealtimeLineSourceAlloter {

    public BackupLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    Segment newSeg(SegmentKey key) {
        IResourceLocation location = new ResourceLocation(new CubePathBuilder(key).asBackup().build(), StoreType.NIO);
        SwiftMetaData metaData = META_SVC.getMetaDataByKey(tableKey.getId());
        return new BackupSegment(location, metaData);
    }
}