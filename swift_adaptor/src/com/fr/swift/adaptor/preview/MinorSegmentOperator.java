package com.fr.swift.adaptor.preview;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.IncreaseSegmentOperator;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/2
 */
class MinorSegmentOperator extends IncreaseSegmentOperator {
    public MinorSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData, List<Segment> segments, String cubeSourceKey) throws SwiftMetaDataException {
        super(sourceKey, metaData, segments, cubeSourceKey);
    }

    @Override
    protected Segment createSegment(int order) {
        String path = String.format("/%s/cubes/%s/minor_seg",
                System.getProperty("user.dir"),
                cubeSourceKey);
        Segment seg = new RealTimeSegmentImpl(new ResourceLocation(path, StoreType.MEMORY), metaData);
        MinorSegmentManager.getInstance().putSegment(sourceKey, Collections.singletonList(seg));
        return seg;
    }
}