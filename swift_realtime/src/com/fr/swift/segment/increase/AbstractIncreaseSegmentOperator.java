package com.fr.swift.segment.increase;

import com.fr.swift.config.ISegmentKey;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.unique.SegmentKeyUnique;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.AbstractSegmentOperator;
import com.fr.swift.segment.HistorySegmentHolder;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.RealtimeSegmentHolder;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentHolder;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-6
 */
public abstract class AbstractIncreaseSegmentOperator extends AbstractSegmentOperator {

    protected List<SegmentHolder> increaseSegmentList;

    public AbstractIncreaseSegmentOperator(SourceKey sourceKey, List<Segment> segments,
                                           String cubeSourceKey, SwiftResultSet swiftResultSet) throws SQLException {
        super(sourceKey, cubeSourceKey, swiftResultSet);
        this.increaseSegmentList = new ArrayList<SegmentHolder>();
        if (null != segments && !segments.isEmpty()) {
            for (int i = 0, len = segments.size(); i < len; i++) {
                if (segments.get(i).getLocation().getStoreType() == Types.StoreType.FINE_IO) {
                    this.segmentList.add(new HistorySegmentHolder(segments.get(i)));
                } else {
                    this.segmentList.add(new RealtimeSegmentHolder(segments.get(i)));
                }
            }
        }
    }

    /**
     * 创建Segment
     * TODO 分块存放目录
     *
     * @param order 块号
     * @return
     * @throws Exception
     * @deprecated fixme 名字换成nextSegment()吧，可能有别的分块规则，和这个不兼容
     */
    @Deprecated
    protected Segment createSegment(int order) {
        String cubePath = System.getProperty("user.dir") + "/cubes/" + cubeSourceKey + "/seg" + order;
        IResourceLocation location = new ResourceLocation(cubePath, Types.StoreType.MEMORY);
        ISegmentKey segmentKey = new SegmentKeyUnique();
        segmentKey.setSegmentOrder(order);
        segmentKey.setUri(location.getUri().getPath());
        segmentKey.setSourceId(sourceKey.getId());
        segmentKey.setStoreType(Types.StoreType.MEMORY.name());
        SegmentConfig.getInstance().addSegment(segmentKey);
        return new RealTimeSegmentImpl(location, metaData);
    }
}
