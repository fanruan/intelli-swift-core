package com.fr.swift.generate.preview;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.increase.IncreaseSegmentOperator;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/2
 */
class MinorSegmentOperator extends IncreaseSegmentOperator {

    public MinorSegmentOperator(SourceKey sourceKey, List<Segment> segments,
                                String cubeSourceKey, SwiftResultSet swiftResultSet) throws SQLException {
        super(sourceKey, segments, cubeSourceKey, swiftResultSet);
    }

    @Override
    protected Segment createSegment(int order, StoreType storeType) {
        String path = String.format("/%s/cubes/%s/minor_seg",
                System.getProperty("user.dir"),
                cubeSourceKey);
        Segment seg = new RealTimeSegmentImpl(new ResourceLocation(path, storeType), metaData);
        MinorSegmentManager.getInstance().putSegment(sourceKey, Collections.singletonList(seg));
        return seg;
    }
}