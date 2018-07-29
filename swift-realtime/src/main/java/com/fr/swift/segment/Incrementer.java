package com.fr.swift.segment;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.service.HistorySegmentPutter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.LimitedResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.structure.ListResultSet;
import com.fr.swift.transatcion.TransactionProxyFactory;

import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class Incrementer implements Inserter {
    private static final SwiftSegmentManager LOCAL_SEGMENTS = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private SwiftSourceAlloter alloter;

    private DataSource dataSource;

    private Segment currentSeg;

    public Incrementer(DataSource dataSource) {
        this(dataSource, new LineSourceAlloter(dataSource.getSourceKey(), new LineAllotRule(LineAllotRule.MEM_STEP)));
    }

    public Incrementer(DataSource dataSource, SwiftSourceAlloter alloter) {
        this.dataSource = dataSource;
        this.alloter = alloter;
    }

    public void increment(SwiftResultSet resultSet) throws SQLException {
        try {
            persistMeta();

            while (resultSet.hasNext()) {
                boolean newSeg = nextSegment();
                Inserter inserter = getInserter();

                int step = ((LineAllotRule) alloter.getAllotRule()).getStep();
                int limit = CubeUtil.isReadable(currentSeg) ? step - currentSeg.getRowCount() : step;
                inserter.insertData(new LimitedResultSet(resultSet, limit, false));

                if (newSeg) {
                    SegmentKey maxSegmentKey = SegmentUtils.getMaxSegmentKey(LOCAL_SEGMENTS.getSegmentKeys(dataSource.getSourceKey()));
                    persistSegment(currentSeg, maxSegmentKey == null ? 0 : maxSegmentKey.getOrder() + 1);
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            resultSet.close();
        }
    }

    private Inserter getInserter() {
        //获得事务代理
        SwiftRealtimeInserter swiftRealtimeInserter = new SwiftRealtimeInserter(currentSeg);
        TransactionProxyFactory proxyFactory = new TransactionProxyFactory(swiftRealtimeInserter.getSwiftBackup().getTransactionManager());
        return (Inserter) proxyFactory.getProxy(swiftRealtimeInserter);
    }

    private Segment newRealtimeSegment(SegmentInfo segInfo, int segCount) {
        String segPath = String.format("%s/seg%d", CubeUtil.getTablePath(dataSource), segCount + segInfo.getOrder());
        return new RealTimeSegmentImpl(new ResourceLocation(segPath, StoreType.MEMORY), dataSource.getMetadata());
    }

    private boolean nextSegment() {
        List<SegmentKey> segmentKeys = LOCAL_SEGMENTS.getSegmentKeys(dataSource.getSourceKey());

        SegmentKey maxSegmentKey = SegmentUtils.getMaxSegmentKey(segmentKeys);
        if (maxSegmentKey == null) {
            currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), 0);
            return true;
        }

        Segment maxSegment = LOCAL_SEGMENTS.getSegment(maxSegmentKey);
        if (maxSegment.isHistory()) {
            currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), maxSegmentKey.getOrder() + 1);
            return true;
        }
        if (alloter.isFull(maxSegment)) {
            currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), maxSegmentKey.getOrder() + 1);
            HistorySegmentPutter.putHistorySegment(maxSegmentKey, maxSegment);
            return true;
        }
        currentSeg = maxSegment;
        return false;
    }

    private void persistMeta() throws SQLException {
        if (!SwiftDatabase.getInstance().existsTable(dataSource.getSourceKey())) {
            SwiftDatabase.getInstance().createTable(dataSource.getSourceKey(), dataSource.getMetadata());
        }
    }

    private void persistSegment(Segment seg, int order) {
        IResourceLocation location = seg.getLocation();
        String path = String.format("%s/seg%d", dataSource.getSourceKey().getId(), order);
        SegmentKey segKey = new SegmentKeyBean(dataSource.getSourceKey().getId(), URI.create(path), order, location.getStoreType(), seg.getMetaData().getSwiftSchema());
        if (!SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).containsSegment(segKey)) {
            SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).addSegments(Collections.singletonList(segKey));
        }
    }

    @Override
    public void insertData(List<Row> rowList) throws SQLException {
        insertData(new ListResultSet(dataSource.getMetadata(), rowList));
    }

    @Override
    public void insertData(SwiftResultSet swiftResultSet) throws SQLException {
        increment(swiftResultSet);
    }

    @Override
    public List<String> getFields() {
        return null;
    }
}