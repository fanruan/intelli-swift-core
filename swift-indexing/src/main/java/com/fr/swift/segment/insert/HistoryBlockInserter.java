package com.fr.swift.segment.insert;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;

import java.net.URI;
import java.util.Collections;

/**
 * @author anchore
 * @date 2018/8/1
 */
public class HistoryBlockInserter extends BaseBlockInserter {
    protected static final SwiftSegmentManager LOCAL_SEGMENTS = SwiftContext.get().getBean("indexingSegmentManager", SwiftSegmentManager.class);
    private SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);

    private int currentDir = 0;

    private int segOrder = 0;

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
        if (entity == null) {
            entity = new SwiftTablePathEntity(sourceKey.getId(), 0);
        } else {
            currentDir = entity.getTablePath() == null ? 0 : entity.getTablePath() + 1;
            entity.setTmpDir(currentDir);
        }
        tablePathService.saveOrUpdate(entity);
    }

    @Override
    protected Inserter getInserter() {
        return new SwiftInserter(currentSeg);
    }

    private Segment newHistorySegment(SegmentInfo segInfo, int segCount) {
        return new HistorySegmentImpl(new ResourceLocation(
                CubeUtil.getHistorySegPath(dataSource, currentDir, segCount + segInfo.getOrder())), dataSource.getMetadata());
    }

    @Override
    protected boolean nextSegment() {
        currentSeg = newHistorySegment(alloter.allot(new LineRowInfo(0)), segOrder++);
        return true;
    }

    @Override
    protected SwiftSegmentManager getSegmentManager() {
        return LOCAL_SEGMENTS;
    }

    @Override
    protected void persistSegment(Segment seg, int order) {
        IResourceLocation location = seg.getLocation();
        String tableKey = dataSource.getSourceKey().getId();
        String path = CubeUtil.getPersistSegPath(dataSource.getSourceKey(), order);
        SegmentKey segKey = new SegmentKeyBean(tableKey, URI.create(path), order, location.getStoreType(), seg.getMetaData().getSwiftDatabase());
        SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).addSegments(Collections.singletonList(segKey));
    }
}