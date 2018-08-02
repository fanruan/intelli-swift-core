package com.fr.swift.segment;

import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.service.HistorySegmentPutter;
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
    public Incrementer(DataSource dataSource) {
        super(dataSource);
    }

    public Incrementer(DataSource dataSource, SwiftSourceAlloter alloter) {
        super(dataSource, alloter);
    }

    @Override
    protected Inserter getInserter() {
        //获得事务代理
        SwiftRealtimeInserter swiftRealtimeInserter = new SwiftRealtimeInserter(currentSeg);
        TransactionProxyFactory proxyFactory = new TransactionProxyFactory(swiftRealtimeInserter.getSwiftBackup().getTransactionManager());
        return (Inserter) proxyFactory.getProxy(swiftRealtimeInserter);
    }

    private Segment newSegment(SegmentInfo segInfo, int segCount) {
        String segPath = CubeUtil.getSegmentPath(dataSource, segCount + segInfo.getOrder());
        return new RealTimeSegmentImpl(new ResourceLocation(segPath, StoreType.MEMORY), dataSource.getMetadata());
    }

    @Override
    protected boolean nextSegment() {
        List<SegmentKey> segmentKeys = LOCAL_SEGMENTS.getSegmentKeys(dataSource.getSourceKey());

        SegmentKey maxSegmentKey = SegmentUtils.getMaxSegmentKey(segmentKeys);
        if (maxSegmentKey == null) {
            currentSeg = newSegment(alloter.allot(new LineRowInfo(0)), 0);
            return true;
        }

        Segment maxSegment = LOCAL_SEGMENTS.getSegment(maxSegmentKey);
        if (maxSegment.isHistory()) {
            currentSeg = newSegment(alloter.allot(new LineRowInfo(0)), maxSegmentKey.getOrder() + 1);
            return true;
        }
        if (alloter.isFull(maxSegment)) {
            currentSeg = newSegment(alloter.allot(new LineRowInfo(0)), maxSegmentKey.getOrder() + 1);
            HistorySegmentPutter.putHistorySegment(maxSegmentKey, maxSegment);
            return true;
        }
        currentSeg = maxSegment;
        return false;
    }
}