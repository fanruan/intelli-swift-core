package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.event.SwiftEventDispatcher;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.property.SwiftProperty;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.cloud.segment.operator.SegmentTransfer;
import com.fr.swift.cloud.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.cloud.segment.operator.insert.SwiftInserter;
import com.fr.swift.cloud.source.DataSource;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.alloter.RowInfo;
import com.fr.swift.cloud.source.alloter.SegmentInfo;
import com.fr.swift.cloud.source.alloter.SwiftSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.SwiftSegmentInfo;
import com.fr.swift.cloud.structure.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author anchore
 * @date 2018/6/5
 */
@SwiftBean(name = "incrementer")
@SwiftScope("prototype")
public class Incrementer<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, SwiftResultSet> {

    private static final SegmentInfo DEFAULT_SEG = new SwiftSegmentInfo(0, Types.StoreType.MEMORY);

    private Pair<Integer, ImmutableBitMap> snapshot;

    private Segment seg;

    public Incrementer(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
    }


    @Override
    protected Inserting getInserting(SegmentKey segKey) {
        seg = SegmentUtils.newSegment(segKey);
        if (seg.isReadable()) {
            snapshot = snapshot(seg);
        }
        return new Inserting(SwiftInserter.ofAppendMode(seg), seg, SegmentUtils.safeGetRowCount(seg));
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        // 增量块已满，transfer掉
        SegmentKey segKey = newSegmentKey(segInfo);
        SegmentKey transferedSeg = new SegmentTransfer(segKey).transfer();
        importSegKeys.clear();
        importSegKeys.add(transferedSeg);
    }

    @Override
    protected void onSucceed() {
        Set<String> segIds = importSegKeys.stream().map(s -> s.getId()).collect(Collectors.toSet());
        if (segLocationSvc.getTableMatchedSegOnNode(SwiftProperty.get().getMachineId(), dataSource.getSourceKey(), segIds).isEmpty()) {
            if (swiftSegmentService.getByIds(segIds).isEmpty()) {
                swiftSegmentService.save(importSegKeys);
            }
            segLocationSvc.saveOnNode(SwiftProperty.get().getMachineId(), new HashSet<>(importSegKeys));
            segmentService.addSegments(importSegKeys);
            SwiftEventDispatcher.asyncFire(SyncSegmentLocationEvent.PUSH_SEG, importSegKeys);
        }
        swiftSegmentService.update(importSegKeys);
        SwiftLoggers.getLogger().debug("incrementer over, save seg location {}", importSegKeys);
    }

    @Override
    protected SegmentInfo allot(int cursor, Row row) {
        return DEFAULT_SEG;
    }

    @Override
    protected void onFailed() {
        // do nothing
    }

    @Override
    public Pair<Integer, ImmutableBitMap> snapshot(Segment segment) {
        return Pair.of(segment.getRowCount(), segment.getAllShowIndex());
    }

    @Override
    public void rollback() {
        if (seg != null) {
            seg.putRowCount(snapshot.getKey());
            seg.putAllShowIndex(snapshot.getValue());
        }
    }
}