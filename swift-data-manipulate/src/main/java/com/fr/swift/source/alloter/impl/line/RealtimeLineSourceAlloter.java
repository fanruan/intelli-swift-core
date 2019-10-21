package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/12/21
 */
public class RealtimeLineSourceAlloter extends BaseLineSourceAlloter {

    static final SwiftMetaDataService META_SVC = SwiftContext.get().getBean(SwiftMetaDataService.class);

    public RealtimeLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected SegmentState getInsertableSeg(int logicOrder) {
        Map<SourceKey, List<SegmentKey>> keyListMap = SEG_SVC.getOwnSegments();
        List<SegmentKey> segKeys = keyListMap.get(tableKey);
        segKeys = segKeys == null ? new ArrayList<SegmentKey>() : segKeys;

        Collections.sort(segKeys, new Comparator<SegmentKey>() {
            @Override
            public int compare(SegmentKey o1, SegmentKey o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });

        for (SegmentKey segKey : segKeys) {
            if (segKey.getStoreType().isPersistent()) {
                continue;
            }
            SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType());
            if (isSegInserting(segInfo)) {
                continue;
            }
            int rowCount = SegmentUtils.safeGetRowCount(newSeg(segKey));

            // todo 暂时限制为未满的块，解除限制会出别的问题
            // TODO: 2019/5/30 anchore 是不是满了的可以直接在这触发transfer
            if (rowCount < rule.getCapacity()) {
                return new SegmentState(segInfo, rowCount - 1);
            }
        }
        // 全是历史块 或 全在inserting 或 全都满了；所以重新new一块
        SegmentKey newSegKey = SEG_SVC.tryAppendSegment(tableKey, Types.StoreType.MEMORY);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(newSegKey.getOrder(), newSegKey.getStoreType());
        return new SegmentState(segInfo);
    }

    /**
     * {@link BackupLineSourceAlloter} 会override，假装去读备份块
     *
     * @param key seg key
     * @return 需要的seg
     */
    Segment newSeg(SegmentKey key) {
        IResourceLocation location = new ResourceLocation(new CubePathBuilder(key).build(), key.getStoreType());
        SwiftMetaData metaData = META_SVC.getMetaDataByKey(tableKey.getId());
        return SwiftContext.get().getBean("realtimeSegment", Segment.class, location, metaData);
    }
}