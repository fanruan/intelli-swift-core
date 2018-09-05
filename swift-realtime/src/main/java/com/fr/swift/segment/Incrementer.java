package com.fr.swift.segment;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.impl.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.service.ScheduledRealtimeTransfer;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;
import com.fr.swift.transatcion.TransactionProxyFactory;

import java.util.List;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class Incrementer extends BaseBlockInserter implements Inserter {
    private static final SwiftSegmentManager LOCAL_SEGMENTS = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    public Incrementer(DataSource dataSource) {
        super(dataSource);
    }

    public Incrementer(DataSource dataSource, SwiftSourceAlloter alloter) {
        super(dataSource, alloter);
    }

    private SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentServiceProvider.class);

    @Override
    protected Inserter getInserter() {
        // 获得事务代理
        SwiftRealtimeInserter swiftRealtimeInserter = new SwiftRealtimeInserter(currentSeg);
        TransactionProxyFactory proxyFactory = new TransactionProxyFactory(swiftRealtimeInserter.getSwiftBackup().getTransactionManager());
        return (Inserter) proxyFactory.getProxy(swiftRealtimeInserter);
    }

    private Segment newRealtimeSegment(SegmentInfo segInfo, int segCount) {
        currentSegKey = new SegmentKeyBean(dataSource.getSourceKey(), segCount + segInfo.getOrder(), StoreType.MEMORY, dataSource.getMetadata().getSwiftDatabase());
        persistSegment();
        ResourceLocation location = new ResourceLocation(CubeUtil.getRealtimeSegPath(dataSource, currentSegKey.getOrder()), StoreType.MEMORY);
        return new RealTimeSegmentImpl(location, dataSource.getMetadata());
    }

    //todo 集群下 segkey order不唯一处理
    @Override
    protected synchronized boolean nextSegment() {
        List<SegmentKey> localSegmentKeys = swiftSegmentService.getOwnSegments().get(dataSource.getSourceKey().getId());
        List<SegmentKey> allSegmentKeys = swiftSegmentService.getAllSegments().get(dataSource.getSourceKey().getId());
        SegmentKey maxLocalSegmentKey = SegmentUtils.getMaxSegmentKey(localSegmentKeys);
        SegmentKey maxAllSegmentKey = SegmentUtils.getMaxSegmentKey(allSegmentKeys);

        if (maxLocalSegmentKey == null) {
            if (maxAllSegmentKey == null) {
                currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), 0);
            } else {
                currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), maxAllSegmentKey.getOrder() + 1);
            }
            return true;
        }
        Segment maxSegment = LOCAL_SEGMENTS.getSegment(maxLocalSegmentKey);

        if (maxSegment.isHistory()) {
            currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), maxAllSegmentKey.getOrder() + 1);
            return true;
        }
        if (alloter.isFull(maxSegment)) {
            currentSeg = newRealtimeSegment(alloter.allot(new LineRowInfo(0)), maxAllSegmentKey.getOrder() + 1);
            new ScheduledRealtimeTransfer.RealtimeToHistoryTransfer(maxLocalSegmentKey).transfer();
            return true;
        }
        currentSeg = maxSegment;
        return false;
    }
}