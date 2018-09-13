package com.fr.swift.segment;

import com.fr.event.EventDispatcher;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.impl.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.transaction.TransactionProxyFactory;

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

    private Segment newRealtimeSegment(SegmentKey segKey) {
        ResourceLocation location = new ResourceLocation(CubeUtil.getRealtimeSegPath(dataSource, segKey.getOrder()), StoreType.MEMORY);
        return new RealTimeSegmentImpl(location, dataSource.getMetadata());
    }

    @Override
    protected boolean nextSegment() {
        List<SegmentKey> localSegmentKeys = swiftSegmentService.getOwnSegments().get(dataSource.getSourceKey().getId());
        SegmentKey maxLocalSegmentKey = SegmentUtils.getMaxSegmentKey(localSegmentKeys);

        if (maxLocalSegmentKey == null) {
            currentSegKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), StoreType.MEMORY);
            currentSeg = newRealtimeSegment(currentSegKey);
            return true;
        }
        Segment maxSegment = LOCAL_SEGMENTS.getSegment(maxLocalSegmentKey);

        if (maxSegment.isHistory()) {
            currentSegKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), StoreType.MEMORY);
            currentSeg = newRealtimeSegment(currentSegKey);
            return true;
        }
        if (alloter.isFull(maxSegment)) {
            currentSegKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), StoreType.MEMORY);
            currentSeg = newRealtimeSegment(currentSegKey);
            EventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, maxLocalSegmentKey);
            return true;
        }
        currentSeg = maxSegment;
        return false;
    }
}