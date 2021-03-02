package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.ColumnTypeUtils;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2019/2/27
 * <p>
 * 区别于realtime segment，会缓存get出来的column
 */
public class CacheColumnSegment extends BaseSegment {

    private final Map<ColumnKey, Column<?>> columns = new ConcurrentHashMap<>();

    public CacheColumnSegment(IResourceLocation location, SwiftMetaData meta) {
        super(location, meta);
    }

    @Override
    public <T> Column<T> getColumn(ColumnKey key) {
        try {
            String name = key.getName();
            String columnId = meta.getColumnId(name);

            if (columns.containsKey(key)) {
                return (Column<T>) columns.get(key);
            }
            synchronized (columns) {
                if (columns.containsKey(key)) {
                    return (Column<T>) columns.get(key);
                }
                IResourceLocation child = location.buildChildLocation(columnId);
                Column<?> column = newColumn(child, ColumnTypeUtils.getClassType(meta.getColumn(name)));
                columns.put(key, column);
                return (Column<T>) column;
            }
        } catch (SwiftMetaDataColumnAbsentException e) {
            SwiftLoggers.getLogger().error("getColumn failed", e);
            return null;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("getColumn failed", e);
            return null;
        }
    }

    @Override
    public void release() {
        try {
            super.release();
        } finally {
            SegmentUtils.releaseHisColumn(columns.values());
        }
    }
}