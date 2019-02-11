package com.fr.swift.segment.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/12/21
 */
@SwiftBean(name = "historyBlockImporter")
@SwiftScope("prototype")
public class HistoryBlockImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A> {

    private final SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);

    private int currentDir = 0;

    public HistoryBlockImporter(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
        init();
    }

    private void init() {
        // todo 考虑导入后的替换，要把mem的考虑进去
        SourceKey sourceKey = dataSource.getSourceKey();
        SwiftTablePathBean entity = tablePathService.get(sourceKey.getId());
        if (entity == null) {
            entity = new SwiftTablePathBean(sourceKey.getId(), 0);
        } else {
            // TODO: 2019/1/24 @anchore 考虑历史和collate共用情况下的path
//            currentDir = entity.getTablePath() == null ? 0 : entity.getTablePath() + 1;
            entity.setTmpDir(currentDir);
        }
//        tablePathService.saveOrUpdate(entity);
    }

    @Override
    protected Inserting getInserting(Segment seg) {
        return new Inserting(new SwiftInserter(seg), seg, 0);
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        // 上传历史块
        // fixme 有临时路径的坑；另外何时上传也要考虑，这里还在导入过程，导入失败怎么办，生成好一块上传一块？
        // todo 考虑异步，提升性能
        try {
            SegmentUtils.indexSegmentIfNeed(Collections.singletonList(insertings.get(segInfo).getSegment()));

            SegmentKey segKey = newSegmentKey(segInfo);
            SwiftEventDispatcher.syncFire(SegmentEvent.UPLOAD_HISTORY, segKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected Segment newSegment(SegmentKey segKey) {
        // todo seg key的其他信息从哪拿
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(currentDir).build(), segKey.getStoreType());
        return SegmentUtils.newSegment(location, dataSource.getMetadata());
    }
}