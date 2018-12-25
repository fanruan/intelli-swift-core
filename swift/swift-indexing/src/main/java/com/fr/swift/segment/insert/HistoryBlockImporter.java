package com.fr.swift.segment.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.util.IoUtil;

import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/12/21
 */
@SwiftBean(name = "historyBlockImporter")
@SwiftScope("prototype")
public class HistoryBlockImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A> {

    private static final SwiftTablePathService TABLE_PATH = SwiftContext.get().getBean(SwiftTablePathService.class);

    private int currentDir = 0;

    public HistoryBlockImporter(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
        init();
    }

    private void init() {
        // todo 考虑导入后的替换，要把mem的考虑进去
        SourceKey sourceKey = dataSource.getSourceKey();
        SwiftTablePathBean entity = TABLE_PATH.get(sourceKey.getId());
        if (entity == null) {
            entity = new SwiftTablePathBean(sourceKey.getId(), 0);
        } else {
            currentDir = entity.getTablePath() == null ? 0 : entity.getTablePath() + 1;
            entity.setTmpDir(currentDir);
        }
        TABLE_PATH.saveOrUpdate(entity);
    }

    @Override
    protected Inserter getInserter(Segment seg) {
        return new SwiftInserter(seg);
    }

    @Override
    protected void releaseFullIfExists() {
        for (Iterator<Inserting> itr = insertings.values().iterator(); itr.hasNext(); ) {
            Inserting inserting = itr.next();
            if (inserting.isFull()) {
                IoUtil.release(inserting);
                itr.remove();
            }
        }
    }

    @Override
    protected Segment newSegment(SegmentKey segKey) {
        // todo seg key的其他信息从哪拿
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(currentDir).build(), segKey.getStoreType());
        return SegmentUtils.newSegment(location, dataSource.getMetadata());
    }
}