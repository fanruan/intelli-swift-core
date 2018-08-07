package com.fr.swift.cube;

import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class CubeUtil {
    public static boolean isReadable(Segment seg) {
        return seg.isReadable();
    }

    public static boolean isReadable(Column col) {
        try {
            col.getDictionaryEncodedColumn().size();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getRealtimeSegPath(DataSource dataSource, int segOrder) {
        return String.format("%s/%s/seg%d",
                dataSource.getMetadata().getSwiftSchema().getDir(),
                dataSource.getSourceKey().getId(), segOrder);
    }

    public static String getHistorySegPath(DataSource dataSource, int segOrder) {
        return getHistorySegPath(dataSource, getCurrentDir(dataSource.getSourceKey()), segOrder);
    }

    public static String getHistorySegPath(DataSource dataSource, int currentDir, int segOrder) {
        return String.format("%s/%d/%s/seg%d",
                dataSource.getMetadata().getSwiftSchema().getDir(),
                currentDir,
                dataSource.getSourceKey().getId(), segOrder);
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