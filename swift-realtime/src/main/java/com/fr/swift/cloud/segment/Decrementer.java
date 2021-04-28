package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.config.service.SwiftSegmentLocationService;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.event.SwiftEventDispatcher;
import com.fr.swift.cloud.lock.SegLocks;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.property.SwiftProperty;
import com.fr.swift.cloud.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.cloud.segment.operator.delete.WhereDeleter;
import com.fr.swift.cloud.source.SourceKey;

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

    /**
     * 删除结构调整
     * 1、先根据table和where，查出可能所在块
     * 2、再在循环和锁内，先判断该块是否存在，若不存在直接continue
     * 3、判断索引andnot之后是否为空，空则说明不需要删除，直接continue
     * 4、最终才是需要删除和put&release的动作，这样既避免并发下删除覆盖的问题呢，又避免重复删除查询，也能降低put&release的概率
     * 5、但是还是有个隐患，高并发场景下，新增块的删除，有几率无法覆盖到，这点目前还是单线程，所以影响不大，后续在考虑这个问题
     * 6、不过由于云端的策略(短时间内不会处理多份同个用户同个月份的数据包)，所以第5点的就目前几乎不会出现
     *
     * @param where
     * @return
     * @throws Exception
     */
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

                if (seg.isHistory()) {
                    seg.release();
                } else {
                    Segment backSegment = SegmentUtils.newBackupSegment(whereSegmentKey);
                    backSegment.putAllShowIndex(allShowIndex);
                    backSegment.release();
                }
                if (allShowIndex.getCardinality() == 0) {
                    removeSegList.add(whereSegmentKey);
                    SwiftLoggers.getLogger().info("{} is empty! deleteing...", whereSegmentKey);
                    if (seg.isHistory()) {
                        swiftSegmentService.delete(whereSegmentKey);
                    }
                    swiftSegmentLocationService.deleteOnNode(SwiftProperty.get().getMachineId(), Collections.singleton(whereSegmentKey));
                    segmentService.removeSegment(whereSegmentKey);
                    SegmentUtils.clearSegment(whereSegmentKey);
                    SwiftEventDispatcher.asyncFire(SyncSegmentLocationEvent.REMOVE_SEG, whereSegmentKey);
                    SwiftLoggers.getLogger().info("{} is empty! delete success!", whereSegmentKey);
                }
            }
        }
        return true;
    }
}
