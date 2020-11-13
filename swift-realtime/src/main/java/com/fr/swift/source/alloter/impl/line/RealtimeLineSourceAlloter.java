package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author anchore
 * @date 2018/12/21
 */
public class RealtimeLineSourceAlloter extends BaseLineSourceAlloter {

    static final SwiftMetaDataService META_SVC = SwiftContext.get().getBean(SwiftMetaDataService.class);

    private static final SegmentInfo DEFAULT_SEG = new SwiftSegmentInfo(0, Types.StoreType.MEMORY);

    public RealtimeLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected int getLogicOrder(LineRowInfo rowInfo) {
        return 0;
    }

    @Override
    protected SegmentState getInsertableSeg(int virtualOrder) {
        SwiftLoggers.getLogger().debug("incrementing, append new mem seg order {} table {}", DEFAULT_SEG.getOrder(), tableKey.getId());
        // TODO: 2020/11/10 Database 提取
        if (swiftSegmentService.getByIds(Stream.of(SwiftSegmentEntity.getId(tableKey, virtualOrder, Types.StoreType.MEMORY)).collect(Collectors.toSet())).isEmpty()) {
            swiftSegmentService.save(new SwiftSegmentEntity(tableKey, virtualOrder, Types.StoreType.MEMORY, SwiftDatabase.REPORT));
        }
        return new SegmentState(DEFAULT_SEG);
    }
}