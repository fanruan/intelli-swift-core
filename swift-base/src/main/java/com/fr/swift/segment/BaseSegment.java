package com.fr.swift.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.DateColumn;
import com.fr.swift.segment.column.impl.DoubleColumn;
import com.fr.swift.segment.column.impl.LongColumn;
import com.fr.swift.segment.column.impl.StringColumn;
import com.fr.swift.segment.relation.CubeMultiRelation;
import com.fr.swift.segment.relation.CubeMultiRelationPath;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.segment.relation.RelationIndexImpl;
import com.fr.swift.segment.relation.column.RelationColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.Util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/1/17
 */
public abstract class BaseSegment implements Segment {
    private static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    private static final String ROW_COUNT = "row_count";

    private static final String ALL_SHOW_INDEX = "all_show_index";

    protected SwiftMetaData meta;
    protected IResourceLocation location;

    private IntWriter rowCountWriter;
    IntReader rowCountReader;

    private BitMapWriter bitMapWriter;
    private BitMapReader bitMapReader;

    private final Map<ColumnKey, Column<?>> columns = new ConcurrentHashMap<ColumnKey, Column<?>>();

    public BaseSegment(IResourceLocation location, SwiftMetaData meta) {
        this.location = location;
        this.meta = meta;
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
            SwiftLoggers.getLogger().error("getColumn failed: {}", Util.getRootCauseMessage(e));
            return null;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("getColumn failed: {}", Util.getRootCauseMessage(e));
            return null;
        }
    }

    protected Column<?> newColumn(IResourceLocation location, ClassType classType) {
        switch (classType) {
            case INTEGER:
            case LONG:
                return new LongColumn(location);
            case DOUBLE:
                return new DoubleColumn(location);
            case DATE:
                return new DateColumn(location);
            case STRING:
                return new StringColumn(location);
            default:
                return Crasher.crash(String.format("cannot new correct column by class type: %s", classType));
        }
    }

    @Override
    public RelationIndex getRelation(CubeMultiRelation relation) {
        SourceKey primarySourceKey = relation.getPrimaryTable();
        String relationKey = relation.getKey();
        return RelationIndexImpl.newRelationIndex(getLocation(), primarySourceKey.getId(), relationKey);
    }

    @Override
    public RelationIndex getRelation(CubeMultiRelationPath relationPath) {
        SourceKey primarySourceKey = relationPath.getStartTable();
        String relationKey = relationPath.getKey();
        return RelationIndexImpl.newRelationIndex(getLocation(), primarySourceKey.getId(), relationKey);
    }

    @Override
    public RelationIndex getRelation(ColumnKey f, CubeMultiRelationPath relationPath) {
        return RelationIndexImpl.newFieldRelationIndex(getLocation(), relationPath.getStartTable().getId(), f.getName());
    }

    @Override
    public SwiftMetaData getMetaData() {
        return meta;
    }

    @Override
    public IResourceLocation getLocation() {
        return location;
    }

    @Override
    public void putRowCount(int rowCount) {
        initRowCountWriter();
        rowCountWriter.put(0, rowCount);
    }

    @Override
    public int getRowCount() {
        initRowCountReader();
        return rowCountReader.get(0);
    }

    @Override
    public void putAllShowIndex(ImmutableBitMap bitMap) {
        initBitMapWriter();
        bitMapWriter.put(0, bitMap);
    }

    @Override
    public ImmutableBitMap getAllShowIndex() {
        initBitMapReader();
        return bitMapReader.get(0);
    }

    private void initRowCountWriter() {
        if (rowCountWriter == null) {
            rowCountWriter = DISCOVERY.getWriter(location.buildChildLocation(ROW_COUNT), new BuildConf(IoType.WRITE, DataType.INT, WriteType.OVERWRITE));
        }
    }

    private void initRowCountReader() {
        if (rowCountReader == null) {
            rowCountReader = DISCOVERY.getReader(location.buildChildLocation(ROW_COUNT), new BuildConf(IoType.READ, DataType.INT));
        }
    }

    private void initBitMapWriter() {
        if (bitMapWriter == null) {
            bitMapWriter = DISCOVERY.getWriter(location.buildChildLocation(ALL_SHOW_INDEX), new BuildConf(IoType.WRITE, DataType.BITMAP, WriteType.OVERWRITE));
        }
    }

    private void initBitMapReader() {
        if (bitMapReader == null) {
            bitMapReader = DISCOVERY.getReader(location.buildChildLocation(ALL_SHOW_INDEX), new BuildConf(IoType.READ, DataType.BITMAP));
        }
    }

    @Override
    public boolean isReadable() {
        initRowCountReader();
        boolean rowCountReadable = rowCountReader.isReadable();
        if (isHistory()) {
            IoUtil.release(rowCountReader);
        }
        rowCountReader = null;
        if (!rowCountReadable) {
            return false;
        }

        initBitMapReader();
        boolean readable = bitMapReader.isReadable();
        if (isHistory()) {
            IoUtil.release(bitMapReader);
        }
        bitMapReader = null;
        return readable;
    }

    @Override
    public void flush() {
        if (rowCountWriter != null) {
            rowCountWriter.flush();
        }
        if (bitMapWriter != null) {
            bitMapWriter.flush();
        }
    }

    @Override
    public void release() {
        IoUtil.release(rowCountWriter, rowCountReader, bitMapWriter, bitMapReader);
        rowCountWriter = null;
        rowCountReader = null;
        bitMapWriter = null;
        bitMapReader = null;
    }

    private Column createRelationColumn(ColumnKey key) {
        return new RelationColumn(key).buildRelationColumn(this);
    }

    @Override
    public boolean isHistory() {
        return location.getStoreType() != StoreType.MEMORY;
    }
}