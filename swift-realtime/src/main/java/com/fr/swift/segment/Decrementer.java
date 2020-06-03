package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private transient static Map<SegmentKey, SegmentKey> SEG_LOCK = new ConcurrentHashMap<>();

    public Decrementer(SourceKey tableKey) {
        this.tableKey = tableKey;
        segmentService = SwiftContext.get().getBean(SegmentService.class);
        swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
    }

    @Override
    public Map<SegmentKey, ImmutableBitMap> delete(Where where) throws Exception {
        List<SegmentKey> removeSegList = new ArrayList<>();
        Table table = SwiftDatabase.getInstance().getTable(tableKey);
        Map<SegmentKey, ImmutableBitMap> indexAfterFilterMap = where.createWhereIndex(table);
        for (Map.Entry<SegmentKey, ImmutableBitMap> entry : indexAfterFilterMap.entrySet()) {
            SegmentKey segKey = entry.getKey();
            synchronized (SEG_LOCK.computeIfAbsent(segKey, n -> segKey)) {
                Segment seg = SegmentUtils.newSegment(segKey);
                ImmutableBitMap indexAfterFilter = entry.getValue();
                ImmutableBitMap originAllShowIndex = seg.getAllShowIndex();
                ImmutableBitMap allShowIndex = originAllShowIndex.getAndNot(indexAfterFilter);
                seg.putAllShowIndex(allShowIndex);
                if (allShowIndex.getCardinality() == 0) {
                    removeSegList.add(segKey);
                }
                if (seg.isHistory()) {
                    if (seg.isHistory()) {
                        seg.release();
                    }
                }
                if (!removeSegList.isEmpty()) {
                    swiftSegmentService.delete(removeSegList);
                    segmentService.removeSegments(removeSegList);
                    SegmentUtils.clearSegments(removeSegList);
                }
            }
        }
        return indexAfterFilterMap;
    }
}
