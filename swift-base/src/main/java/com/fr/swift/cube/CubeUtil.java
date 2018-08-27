package com.fr.swift.cube;

import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.third.guava.base.Optional;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class CubeUtil {
    public static boolean isReadable(Segment seg) {
        return seg.isReadable();
    }

    public static String getPersistSegPath(SourceKey tableKey, int segOrder) {
        return String.format("%s/seg%d", tableKey.getId(), segOrder);
    }

    public static String getRealtimeSegPath(DataSource dataSource, int segOrder) {
        return getSegPath(dataSource.getMetadata().getSwiftSchema(), Optional.<Integer>absent(), dataSource.getSourceKey(), segOrder);
    }

    public static String getHistorySegPath(DataSource dataSource, int segOrder) {
        return getHistorySegPath(dataSource, getCurrentDir(dataSource.getSourceKey()), segOrder);
    }

    public static String getHistorySegPath(DataSource dataSource, int currentDir, int segOrder) {
        return getSegPath(dataSource.getMetadata().getSwiftSchema(), Optional.of(currentDir), dataSource.getSourceKey(), segOrder);
    }

    public static String getSegPath(SegmentKey segKey) {
        return getSegPath(segKey, false);
    }

    public static String getSegPath(SegmentKey segKey, boolean logicalPath) {
        SourceKey tableKey = segKey.getTable();
        Optional<Integer> currentDir = logicalPath || segKey.getStoreType() == StoreType.MEMORY ?
                Optional.<Integer>absent() :
                Optional.of(getCurrentDir(tableKey));
        return getSegPath(segKey.getSwiftSchema(), currentDir, tableKey, segKey.getOrder());
    }

    public static String getSegPath(SwiftDatabase swiftSchema, Optional<Integer> currentDir, SourceKey tableKey, int segOrder) {
        String schemaDir = swiftSchema.getDir();
        String tableId = tableKey.getId();
        return currentDir.isPresent() ?
                String.format("%s/%d/%s/seg%d", schemaDir, currentDir.get(), tableId, segOrder) :
                String.format("%s/%s/seg%d", schemaDir, tableId, segOrder);
    }

    public static String getColumnPath(SegmentKey segKey, String columnId) {
        return String.format("%s/%s", getSegPath(segKey), columnId);
    }

    public static String getAbsoluteColumnPath(SegmentKey segKey, String columnId) {
        return String.format("/%s/%s", getAbsoluteSegPath(segKey), columnId);
    }

    public static String getAbsoluteSegPath(SegmentKey segKey) {
        return String.format("/%s/%s", SwiftContext.get().getBean(SwiftCubePathService.class).getSwiftPath(), getSegPath(segKey));
    }

    public static int getCurrentDir(SourceKey tableKey) {
        SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
        SwiftTablePathEntity entity = tablePathService.get(tableKey.getId());
        if (entity == null || entity.getTablePath() == null) {
            return 0;
        }
        return entity.getTablePath();
    }
}