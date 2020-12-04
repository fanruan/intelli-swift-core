package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.lock.SegLocks;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "decrementer")
@SwiftScope("prototype")
public class Decrementer implements WhereDeleter {

    private SourceKey tableKey;

    private final SegmentService segmentService;

    private final SwiftSegmentService swiftSegmentService;

    private final SwiftSegmentLocationService swiftSegmentLocationService;

    public Decrementer(SourceKey tableKey) {
        this.tableKey = tableKey;
        segmentService = SwiftContext.get().getBean(SegmentService.class);
        swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        swiftSegmentLocationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
    }

    @Override
    public Map<SegmentKey, ImmutableBitMap> delete(Where where) throws Exception {
        List<SegmentKey> removeSegList = new ArrayList<>();
        Table table = SwiftDatabase.getInstance().getTable(tableKey);
        Map<SegmentKey, ImmutableBitMap> indexAfterFilterMap = where.createWhereIndex(table);
        for (Map.Entry<SegmentKey, ImmutableBitMap> entry : indexAfterFilterMap.entrySet()) {
            SegmentKey segKey = entry.getKey();
            synchronized (SegLocks.SEG_LOCK.computeIfAbsent(segKey, n -> segKey)) {
                // TODO: 2020/11/10 备份块删除
                Segment seg = SegmentUtils.newSegment(segKey);
                ImmutableBitMap indexAfterFilter = entry.getValue();
                ImmutableBitMap originAllShowIndex = seg.getAllShowIndex();
                ImmutableBitMap allShowIndex = originAllShowIndex.getAndNot(indexAfterFilter);
                seg.putAllShowIndex(allShowIndex);
                SwiftLoggers.getLogger().info("{} rows data has been deleted in seg {}!"
                        , originAllShowIndex.getCardinality() - allShowIndex.getCardinality(), segKey);
                if (allShowIndex.getCardinality() == 0) {
                    removeSegList.add(segKey);
                    SwiftLoggers.getLogger().info("{} is empty! deleteing...", segKey);
                    if (seg.isHistory()) {
                        swiftSegmentService.delete(segKey);
                    }
                    swiftSegmentLocationService.deleteOnNode(SwiftProperty.get().getMachineId(), Collections.singleton(segKey));
                    segmentService.removeSegment(segKey);
                    SegmentUtils.clearSegment(segKey);
                    SwiftLoggers.getLogger().info("{} is empty! delete success!", segKey);
                }

                if (seg.isHistory()) {
                    seg.release();
                } else {
                    Segment backSegment = SegmentUtils.newBackupSegment(segKey);
                    backSegment.putAllShowIndex(allShowIndex);
                    backSegment.release();
                }
            }
        }
        return indexAfterFilterMap;
    }
}
