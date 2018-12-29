package com.fr.swift.segment;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.transaction.TransactionProxyFactory;

/**
 * @author anchore
 * @date 2018/6/5
 */
@SwiftBean(name = "incrementer")
@SwiftScope("prototype")
public class Incrementer<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A> {

    public Incrementer(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
    }

    @Override
    protected Inserter getInserter(Segment seg) {
        // 获得事务代理
        SwiftRealtimeInserter swiftRealtimeInserter = new SwiftRealtimeInserter(seg);
        TransactionProxyFactory proxyFactory = new TransactionProxyFactory(swiftRealtimeInserter.getSwiftBackup().getTransactionManager());
        return (Inserter) proxyFactory.getProxy(swiftRealtimeInserter);
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        // 增量块已满，transfer掉
        SegmentKey segKey = newSegmentKey(segInfo);
        SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, segKey);
    }

    @Override
    protected Segment newSegment(SegmentKey segKey) {
        // todo seg key的其他信息从哪拿
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).build(), segKey.getStoreType());
        return SegmentUtils.newSegment(location, dataSource.getMetadata());
    }
}