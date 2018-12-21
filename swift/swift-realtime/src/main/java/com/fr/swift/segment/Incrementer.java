package com.fr.swift.segment;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.transaction.TransactionProxyFactory;
import com.fr.swift.util.IoUtil;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/6/5
 */
@SwiftBean(name = "incrementer")
@SwiftScope("prototype")
public class Incrementer<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockInserter<A> {

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
    protected void releaseFullIfExists() {
        for (Iterator<Entry<SegmentInfo, Inserting>> itr = insertings.entrySet().iterator(); itr.hasNext(); ) {
            Entry<SegmentInfo, Inserting> entry = itr.next();
            Inserting inserting = entry.getValue();
            if (inserting.isFull()) {
                IoUtil.release(inserting);
                itr.remove();

                // 增量块已满，transfer掉
                SegmentInfo segInfo = entry.getKey();
                SegmentKey segKey = new SegmentKeyBean(dataSource.getSourceKey(), segInfo.getOrder(), segInfo.getStoreType(), dataSource.getMetadata().getSwiftDatabase());
                SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, segKey);
            }
        }
    }

    @Override
    protected Segment newSegment(SegmentInfo segInfo) {
        // todo seg key的其他信息从哪拿
        SegmentKey segKey = new SegmentKeyBean(dataSource.getSourceKey(), segInfo.getOrder(), segInfo.getStoreType(), dataSource.getMetadata().getSwiftDatabase());
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).build(), segKey.getStoreType());
        return SegmentUtils.newSegment(location, dataSource.getMetadata());
    }
}