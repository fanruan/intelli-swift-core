package com.fr.swift.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscoveryImpl;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.DateColumn;
import com.fr.swift.segment.column.impl.DoubleColumn;
import com.fr.swift.segment.column.impl.LongColumn;
import com.fr.swift.segment.column.impl.StringColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.segment.relation.RelationIndexImpl;
import com.fr.swift.source.ColumnTypeConstants.CLASS;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/1/17
 */
public abstract class BaseSegment implements Segment {
    private static final ResourceDiscovery DISCOVERY = ResourceDiscoveryImpl.getInstance();

    protected SwiftMetaData meta;
    protected IResourceLocation parent;

    private IntWriter rowCountWriter;
    private IntReader rowCountReader;

    private BitMapWriter bitMapWriter;
    private BitMapReader bitMapReader;

    private final ConcurrentHashMap<ColumnKey, Column<?>> columns = new ConcurrentHashMap<ColumnKey, Column<?>>();

    public BaseSegment(IResourceLocation parent, SwiftMetaData meta) {
        this.parent = parent;
        this.meta = meta;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Column<T> getColumn(ColumnKey key) {
        if (columns.containsKey(key)) {
            return (Column<T>) columns.get(key);
        }
        synchronized (columns) {
            if (columns.containsKey(key)) {
                return (Column<T>) columns.get(key);
            }
            IResourceLocation child = parent.buildChildLocation(key.getName());
            Column<?> column = newColumn(child, getClassType(key));
            columns.put(key, column);
            return (Column<T>) column;
        }
    }

    private static Column<?> newColumn(IResourceLocation location, int classType) {
        switch (classType) {
            case CLASS.INTEGER:
//                return new IntColumn(location);
            case CLASS.LONG:
                return new LongColumn(location);
            case CLASS.DOUBLE:
                return new DoubleColumn(location);
            case CLASS.DATE:
                return new DateColumn(location);
            case CLASS.STRING:
                return new StringColumn(location);
            default:
        }
        return Crasher.crash(String.format("cannot new correct column by class type: %d", classType));
    }

    private int getClassType(ColumnKey key) {
        try {
            for (int i = 1, len = meta.getColumnCount(); i <= len; i++) {
                if (meta.getColumnName(i).equals(key.getName())) {
                    return ColumnTypeUtils.sqlTypeToClassType(
                            meta.getColumnType(i),
                            meta.getPrecision(i),
                            meta.getScale(i));
                }
            }
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().error(e);
        }
        return -1;
    }

    @Override
    public RelationIndex getRelation(CubeMultiRelation relation) {
        SourceKey primarySourceKey = relation.getPrimaryTable();
        String relationKey = relation.getKey();
        return new RelationIndexImpl(getLocation(), primarySourceKey.getId(), relationKey);
    }

    @Override
    public RelationIndex getRelation(CubeMultiRelationPath relationPath) {
        SourceKey primarySourceKey = relationPath.getStartTable();
        String relationKey = relationPath.getKey();
        return new RelationIndexImpl(getLocation(), primarySourceKey.getId(), relationKey);
    }

    @Override
    public IResourceLocation getLocation() {
        return parent;
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
    public SwiftMetaData getMetaData() {
        return meta;
    }

    @Override
    public ImmutableBitMap getAllShowIndex() {
        initBitMapReader();
        return bitMapReader.get(0);
    }

    private void initRowCountWriter() {
        if (rowCountWriter == null) {
            rowCountWriter = (IntWriter) DISCOVERY.getWriter(parent.buildChildLocation(ROW_COUNT), new BuildConf(IoType.WRITE, DataType.INT));
        }
    }

    private void initRowCountReader() {
        if (rowCountReader == null) {
            rowCountReader = (IntReader) DISCOVERY.getReader(parent.buildChildLocation(ROW_COUNT), new BuildConf(IoType.READ, DataType.INT));
        }
    }

    private void initBitMapWriter() {
        if (bitMapWriter == null) {
            bitMapWriter = (BitMapWriter) DISCOVERY.getWriter(parent.buildChildLocation(ALL_SHOW_INDEX), new BuildConf(IoType.WRITE, DataType.BITMAP));
        }
    }

    private void initBitMapReader() {
        if (bitMapReader == null) {
            bitMapReader = (BitMapReader) DISCOVERY.getReader(parent.buildChildLocation(ALL_SHOW_INDEX), new BuildConf(IoType.READ, DataType.BITMAP));
        }
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
        if (rowCountWriter != null) {
            rowCountWriter.release();
            rowCountWriter = null;
        }
        if (rowCountReader != null) {
            rowCountReader.release();
            rowCountReader = null;
        }
        if (bitMapWriter != null) {
            bitMapWriter.release();
            bitMapWriter = null;
        }
        if (bitMapReader != null) {
            bitMapReader.release();
            bitMapReader = null;
        }
    }


}