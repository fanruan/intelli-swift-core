package com.fr.swift.segment.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/8/1
 */
@SwiftBean(name = "historyBlockInserter")
@SwiftScope("prototype")
public class HistoryBlockInserter extends BaseBlockInserter {
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
        SwiftTablePathBean entity = tablePathService.get(sourceKey.getId());
        if (entity == null) {
            entity = new SwiftTablePathBean(sourceKey.getId(), 0);
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
        currentSegKey = new SegmentKeyBean(dataSource.getSourceKey(), segCount + segInfo.getOrder(), StoreType.FINE_IO, dataSource.getMetadata().getSwiftDatabase());
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(currentSegKey).setTempDir(currentDir).build(), currentSegKey.getStoreType());
        return new HistorySegmentImpl(location, dataSource.getMetadata());
    }

    @Override
    protected boolean nextSegment() {
        // fixme 分布式 多节点导入也会竞争segOrder
        currentSeg = newHistorySegment(alloter.allot(new LineRowInfo(0)), segOrder++);

        SEG_SVC.addSegments(Collections.singletonList(currentSegKey));
        return true;
    }

    @Override
    protected void clearDirtySegIfNeed() {
        if (currentSegKey != null) {
            SEG_SVC.removeSegments(Collections.singletonList(currentSegKey));
            SegmentUtils.clearSegment(currentSegKey);
        }
    }
}