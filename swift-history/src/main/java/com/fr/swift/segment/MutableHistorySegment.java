package com.fr.swift.segment;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/10/30
 */
public class MutableHistorySegment extends BaseSegment implements HistorySegment {

    private final Map<ColumnKey, Column<?>> columns = new ConcurrentHashMap<ColumnKey, Column<?>>();

    public MutableHistorySegment(IResourceLocation location, SwiftMetaData meta) {
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
            if (key.getRelation() != null) {
                return createRelationColumn(key);
            }
            SwiftLoggers.getLogger().error("getColumn failed", e);
            return null;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("getColumn failed", e);
            return null;
        }
    }
}