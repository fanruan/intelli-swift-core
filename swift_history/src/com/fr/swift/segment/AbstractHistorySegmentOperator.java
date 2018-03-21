package com.fr.swift.segment;

import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.ISegmentKey;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.unique.SegmentKeyUnique;
import com.fr.swift.config.unique.SegmentUnique;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-6
 */
public abstract class AbstractHistorySegmentOperator extends AbstractSegmentOperator {
    public AbstractHistorySegmentOperator(SourceKey sourceKey,
                                          String cubeSourceKey, SwiftResultSet swiftResultSet) throws SQLException {
        super(sourceKey, cubeSourceKey, swiftResultSet);
        configSegment = new SegmentUnique();
        configSegment.setSourceKey(sourceKey.getId());
    }

    protected IConfigSegment configSegment;

    /**
     * 创建Segment
     * TODO 分块存放目录
     *
     * @param order 块号
     * @return
     * @throws Exception
     */
    protected Segment createSegment(int order) {
        String cubePath = System.getProperty("user.dir") + "/cubes/" + cubeSourceKey + "/seg" + order;
        IResourceLocation location = new ResourceLocation(cubePath);
        ISegmentKey segmentKey = new SegmentKeyUnique();
        segmentKey.setSegmentOrder(order);
        segmentKey.setUri(location.getUri().getPath());
        segmentKey.setSourceId(sourceKey.getId());
        segmentKey.setStoreType(Types.StoreType.FINE_IO.name());
        configSegment.addSegment(segmentKey);
        return new HistorySegmentImpl(location, metaData);
    }
}
