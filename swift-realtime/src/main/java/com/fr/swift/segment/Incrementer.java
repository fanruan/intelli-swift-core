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
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.line.LineAllotRule;
import com.fr.swift.source.alloter.line.LineRowInfo;
import com.fr.swift.source.alloter.line.LineSourceAlloter;
import com.fr.swift.structure.ListResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class Incrementer implements Inserter {
    private static final SwiftSegmentManager LOCAL_SEGMENT_PROVIDER = SwiftContext.getInstance().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private SwiftSourceAlloter alloter;

    private DataSource dataSource;

    private Segment currentSeg;

    public Incrementer(DataSource dataSource) {
        this.dataSource = dataSource;
        alloter = new LineSourceAlloter(dataSource.getSourceKey());
    }

    public void increment(SwiftResultSet resultSet) throws SQLException {
        try {
            persistMeta();

            int count = LOCAL_SEGMENT_PROVIDER.getSegmentKeys(dataSource.getSourceKey()).size();

            //todo 这next第一行就没了
//            while (resultSet.next()) {
            boolean newSeg = nextSegment();
            Inserter inserter = new SwiftRealtimeInserter(currentSeg);
            int step = ((LineAllotRule) alloter.getAllotRule()).getStep();
            int limit = CubeUtil.isReadable(currentSeg) ? step - currentSeg.getRowCount() : step;
            inserter.insertData(new LimitedResultSet(resultSet, limit));

            if (newSeg) {
                persistSegment(currentSeg, count++);
            }
//            }
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            resultSet.close();
        }
    }

    private Segment newRealtimeSegment(SegmentInfo segInfo, int segCount) {
        String segPath = String.format("%s/seg%d", CubeUtil.getTablePath(dataSource), segCount + segInfo.getOrder());
        return new RealTimeSegmentImpl(new ResourceLocation(segPath, StoreType.MEMORY), dataSource.getMetadata());
    }

    private boolean nextSegment() {
        List<SegmentKey> segmentKeys = LOCAL_SEGMENT_PROVIDER.getSegmentKeys(dataSource.getSourceKey());
        if (segmentKeys.isEmpty() ||
                segmentKeys.get(segmentKeys.size() - 1).getStoreType() != StoreType.MEMORY) {
            currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), segmentKeys.size());
            return true;
        }
        currentSeg = LOCAL_SEGMENT_PROVIDER.getSegment(segmentKeys.get(segmentKeys.size() - 1));
        return false;
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

    @Override
    public List<Segment> insertData(List<Row> rowList) throws SQLException {
        return insertData(new ListResultSet(dataSource.getMetadata(), rowList));
    }

    @Override
    public List<Segment> insertData(SwiftResultSet swiftResultSet) throws SQLException {
        increment(swiftResultSet);
        return Collections.emptyList();
    }

    @Override
    public List<String> getFields() {
        return null;
    }
}