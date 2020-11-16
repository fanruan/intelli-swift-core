package com.fr.swift.segment.backup;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.BackupSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

import java.util.Collections;

/**
 * @author anchore
 * @date 2019/3/8
 */
public class BackupBlockImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, SwiftResultSet> {

    private static final SegmentInfo DEFAULT_SEG_INFO = new SwiftSegmentInfo(0, StoreType.NIO);

    public BackupBlockImporter(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
    }

    @Override
    protected Inserting getInserting(SegmentKey segKey) {

        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).asBackup().build(), StoreType.NIO);
        Segment backupSeg = new BackupSegment(location, dataSource.getMetadata());

//        // 备份包裹事务
//        TransactionManager transactionManager = SwiftContext.get().getBean("transactionManager", TransactionManager.class, backupSeg);
//        transactionManager.setOldAttach(realtimeSeg);
//        TransactionProxyFactory proxyFactory = new TransactionProxyFactory(transactionManager);

        Inserter inserter = SwiftContext.get().getBean(Inserter.class, backupSeg, true);

        return new Inserting(inserter, backupSeg, SegmentUtils.safeGetRowCount(backupSeg));
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        // do nothing
    }

    @Override
    protected void onSucceed() {
//        for (SegmentKey importSegKey : importSegKeys) {
//            segLocationSvc.saveOnNode(SwiftProperty.get().getMachineId(), Collections.singleton(importSegKey));
//        }
    }

    @Override
    protected SegmentInfo allot(int cursor, Row row) {
        return DEFAULT_SEG_INFO;
    }

    @Override
    protected void onFailed() {
        // do nothing
    }
}