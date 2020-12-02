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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public boolean delete(Where where) throws Exception {
        List<SegmentKey> removeSegList = new ArrayList<>();
        Table table = SwiftDatabase.getInstance().getTable(tableKey);
        Collection<SegmentKey> whereSegments = where.createWhereSegments(table);

        for (SegmentKey whereSegmentKey : whereSegments) {
            synchronized (SegLocks.SEG_LOCK.computeIfAbsent(whereSegmentKey, n -> whereSegmentKey)) {
                if (!segmentService.exist(whereSegmentKey)) {
                    //segment不存在了，就不删了
                    continue;
                }
                Segment seg = SegmentUtils.newSegment(whereSegmentKey);
                ImmutableBitMap indexAfterFilter = where.createWhereIndex(table, whereSegmentKey);
                ImmutableBitMap originAllShowIndex = seg.getAllShowIndex();
                ImmutableBitMap allShowIndex = originAllShowIndex.getAndNot(indexAfterFilter);
                //原始allshow量和删除后allshow量保持一致，则认为没有需要删除的内容，直接进行下一块seg.getAllShowIndex().getCardinality()
                if (originAllShowIndex.getAndNot(allShowIndex).isEmpty()) {
                    SwiftLoggers.getLogger().info("0 rows data has been deleted in seg {}!", whereSegmentKey);
                    continue;
                }
                seg.putAllShowIndex(allShowIndex);
                SwiftLoggers.getLogger().info("{} rows data has been deleted in seg {}!"
                        , originAllShowIndex.getCardinality() - allShowIndex.getCardinality(), whereSegmentKey);
                if (allShowIndex.getCardinality() == 0) {
                    removeSegList.add(whereSegmentKey);
                    SwiftLoggers.getLogger().info("{} is empty! deleteing...", whereSegmentKey);
                    if (seg.isHistory()) {
                        swiftSegmentService.delete(whereSegmentKey);
                    }
                    swiftSegmentLocationService.deleteOnNode(SwiftProperty.get().getMachineId(), Collections.singleton(whereSegmentKey));
                    segmentService.removeSegment(whereSegmentKey);
                    SegmentUtils.clearSegment(whereSegmentKey);
                    SwiftLoggers.getLogger().info("{} is empty! delete success!", whereSegmentKey);
                }
                if (seg.isHistory()) {
                    seg.release();
                } else {
                    Segment backSegment = SegmentUtils.newBackupSegment(whereSegmentKey);
                    backSegment.putAllShowIndex(allShowIndex);
                    backSegment.release();
                }
            }
        }
        return true;
    }
}
