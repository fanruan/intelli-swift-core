package com.fr.swift.segment;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.impl.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.transaction.TransactionProxyFactory;
import com.fr.third.guava.base.Optional;

import java.util.ArrayList;
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
        Optional<SegmentKey> maxLocalSegmentKey = SegmentUtils.getMaxSegmentKey(filterRealtime(localSegmentKeys));

        if (!maxLocalSegmentKey.isPresent()) {
            currentSegKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), StoreType.MEMORY);

            SwiftLoggers.getLogger().info("max seg is absent, append new seg {}", currentSegKey);

            currentSeg = newRealtimeSegment(currentSegKey);
            SegmentContainer.NORMAL.updateSegment(currentSegKey, currentSeg);
            return true;
        }
        Segment maxSegment = LOCAL_SEGMENTS.getSegment(maxLocalSegmentKey.get());

        if (alloter.isFull(maxSegment)) {
            currentSegKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), StoreType.MEMORY);

            SwiftLoggers.getLogger().info("max seg {} is full, append new seg {}", maxLocalSegmentKey.get(), currentSegKey);

            currentSeg = newRealtimeSegment(currentSegKey);
            SegmentContainer.NORMAL.updateSegment(currentSegKey, currentSeg);
            SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, maxLocalSegmentKey.get());
            return true;
        }
        currentSegKey = maxLocalSegmentKey.get();
        currentSeg = maxSegment;
        return false;
    }

    private static List<SegmentKey> filterRealtime(List<SegmentKey> segKeys) {
        if (segKeys == null) {
            return null;
        }
        List<SegmentKey> realtimeSegKeys = new ArrayList<SegmentKey>();
        for (SegmentKey segKey : segKeys) {
            if (segKey.getStoreType().isTransient()) {
                realtimeSegKeys.add(segKey);
            }
        }
        return realtimeSegKeys;
    }
}