package com.fr.swift.segment;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.line.LineAllotRule;
import com.fr.swift.source.alloter.line.LineRowInfo;
import com.fr.swift.source.alloter.line.LineSourceAlloter;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class Incrementer {
    private static final SwiftSegmentManager LOCAL_SEGMENT_PROVIDER = SwiftContext.getInstance().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private SwiftSourceAlloter alloter;

    private DataSource dataSource;

    public Incrementer(SourceKey tableKey) {
        alloter = new LineSourceAlloter(tableKey);
        try {
            dataSource = SwiftDatabase.getInstance().getTable(tableKey);
        } catch (SQLException e) {
            Crasher.crash(e);
        }
    }

    public void increment(SwiftResultSet resultSet) throws SQLException {
        try {
            persistMeta();

            int count = LOCAL_SEGMENT_PROVIDER.getSegmentKeys(dataSource.getSourceKey()).size();

            while (resultSet.next()) {
                Segment seg = getCurrentSegment();
                Inserter inserter = new SwiftInserter(seg);
                int step = ((LineAllotRule) alloter.getAllotRule()).getStep();
                int limit = CubeUtil.isReadable(seg) ? step - seg.getRowCount() : step;
                inserter.insertData(new LimitedResultSet(resultSet, limit));

                persistSegment(seg, ++count);
            }
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            resultSet.close();
        }
    }

    private void persistMeta() throws SQLException {
        if (!SwiftDatabase.getInstance().existsTable(dataSource.getSourceKey())) {
            SwiftDatabase.getInstance().createTable(dataSource.getSourceKey(), dataSource.getMetadata());
        }
    }

    private void persistSegment(Segment seg, int order) {
        IResourceLocation location = seg.getLocation();
        SegmentKey segKey = new SegmentKeyBean(dataSource.getSourceKey().getId(), location.getUri(), order, location.getStoreType());
        if (!SwiftSegmentServiceProvider.getProvider().containsSegment(segKey)) {
            SwiftSegmentServiceProvider.getProvider().addSegments(Collections.singletonList(segKey));
        }
    }

    private Segment newRealtimeSegment(SegmentInfo segInfo, int segCount) {
        String segPath = String.format("%s/seg%d", CubeUtil.getTablePath(dataSource), segCount + segInfo.getOrder());
        return new RealTimeSegmentImpl(new ResourceLocation(segPath, StoreType.MEMORY), dataSource.getMetadata());
    }

    private Segment getCurrentSegment() {
        List<SegmentKey> segmentKeys = LOCAL_SEGMENT_PROVIDER.getSegmentKeys(dataSource.getSourceKey());
        if (segmentKeys.isEmpty() ||
                segmentKeys.get(segmentKeys.size() - 1).getStoreType() != StoreType.MEMORY) {
            return newRealtimeSegment(alloter.allot(new LineRowInfo(0)), segmentKeys.size());
        }
        return LOCAL_SEGMENT_PROVIDER.getSegment(segmentKeys.get(segmentKeys.size() - 1));
    }
}