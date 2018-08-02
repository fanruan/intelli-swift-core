package com.fr.swift.segment.insert;

import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;

import java.util.List;

/**
 * @author anchore
 * @date 2018/8/1
 */
public class HistoryBlockInserter extends BaseBlockInserter {
    private SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);

    private int cubeTmpPath;

    public HistoryBlockInserter(DataSource dataSource) {
        super(dataSource);
        init();
    }

    public HistoryBlockInserter(DataSource dataSource, SwiftSourceAlloter alloter) {
        super(dataSource, alloter);
        init();
    }

    private void init() {
        SourceKey sourceKey = dataSource.getSourceKey();
        SwiftTablePathEntity entity = tablePathService.get(sourceKey.getId());
        cubeTmpPath = entity == null ? 0 : entity.getTablePath() + 1;
        tablePathService.saveOrUpdate(new SwiftTablePathEntity(sourceKey.getId(), cubeTmpPath));
    }

    @Override
    protected Inserter getInserter() {
        return new SwiftInserter(currentSeg);
    }

    private Segment newSegment(SegmentInfo segInfo, int segCount) {
        String segPath = String.format("%s/%d/%s/seg%d",
                dataSource.getMetadata().getSwiftSchema().getDir(),
                cubeTmpPath,
                dataSource.getSourceKey().getId(),
                segCount + segInfo.getOrder());
        return new HistorySegmentImpl(new ResourceLocation(segPath), dataSource.getMetadata());
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
        if (alloter.isFull(maxSegment)) {
            currentSeg = newSegment(alloter.allot(new LineRowInfo(0)), maxSegmentKey.getOrder() + 1);
            return true;
        }
        currentSeg = maxSegment;
        return false;
    }
}