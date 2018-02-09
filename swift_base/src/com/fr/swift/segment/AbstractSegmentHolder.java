package com.fr.swift.segment;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class created on 2018-1-19 14:30:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractSegmentHolder implements ISegmentHolder {

    protected Segment segment;
    protected Types.StoreType storeType;
    protected Map<ColumnKey, MutableBitMap> nullMap;

    protected  AtomicInteger rowCount;
    protected SwiftMetaData metaData;

    public AbstractSegmentHolder(Segment segment) throws SwiftMetaDataException {
        this.segment = segment;
        this.rowCount = new AtomicInteger(0);
        this.metaData = segment.getMetaData();
        this.storeType = segment.getLocation().getStoreType();
        init();
    }

    private void init() throws SwiftMetaDataException {
        this.nullMap = new ConcurrentHashMap<ColumnKey, MutableBitMap>();
        for (int i = 1, len = metaData.getColumnCount(); i <= len; i++) {
            ColumnKey columnKey = new ColumnKey(metaData.getColumnName(i));
            this.nullMap.put(columnKey, BitMaps.newRoaringMutable());
        }
    }

    @Override
    public Segment getSegment() {
        return segment;
    }

    @Override
    public DetailColumn getColumn(String columnName) {
        return getColumn(new ColumnKey(columnName));
    }

    @Override
    public DetailColumn getColumn(ColumnKey columnKey) {
        return segment.getColumn(columnKey).getDetailColumn();
    }

    private void setNullIndex(ColumnKey columnKey, int row) {
        MutableBitMap nullIndex = this.nullMap.get(columnKey);
        if (null == nullIndex) {
            nullIndex = BitMaps.newRoaringMutable();
        }
        nullIndex.add(row);
        this.nullMap.put(columnKey, nullIndex);
    }

    @Override
    public int incrementRowCount() {
        return rowCount.incrementAndGet();
    }

    @Override
    public void putRowCount() {
        segment.putRowCount(rowCount.get());
    }

    @Override
    public void putAllShowIndex() {
        segment.putAllShowIndex(BitMaps.newAllShowBitMap(rowCount.get()));
    }

    @Override
    public void putNullIndex() {
        Iterator<ColumnKey> iterator = nullMap.keySet().iterator();
        while (iterator.hasNext()) {
            ColumnKey key = iterator.next();
            segment.getColumn(key).getBitmapIndex().putNullIndex(nullMap.get(key));
        }
    }

    @Override
    public void putDetail(int column, Object value) throws SwiftMetaDataException {
        ClassType clazz = getClassType(metaData.getColumnType(column + 1), metaData.getPrecision(column + 1), metaData.getScale(column + 1));
        ColumnKey key = new ColumnKey(metaData.getColumnName(column + 1));
        DetailColumn detail = getColumn(key);
        int row = rowCount.get();
        if (null == value) {
            detail.put(row, getNullValue(clazz));
            setNullIndex(key, row);
        } else {
            detail.put(row, value);
        }
    }

    private ClassType getClassType(int sqlType, int precision, int scale) {
        return ColumnTypeUtils.sqlTypeToClassType(sqlType, precision, scale);
    }

    private Object getNullValue(ClassType clazz) {
        switch (clazz) {
            case INTEGER:
                return IOConstant.NULL_LONG;
            case DATE:
            case LONG:
                return IOConstant.NULL_LONG;
            case DOUBLE:
                return IOConstant.NULL_DOUBLE;
            case STRING:
                return IOConstant.NULL_STRING;
            default:
                return Crasher.crash("Invalid type: " + clazz);
        }
    }

    @Override
    public void release() {
        try {
            for (int i = 1, len = metaData.getColumnCount(); i <= len; i++) {
                segment.getColumn(new ColumnKey(metaData.getColumnName(i))).getBitmapIndex().release();
                getColumn(metaData.getColumnName(i)).release();
            }
        } catch (Exception e) {
            Crasher.crash(e);
        } finally {
            segment.release();
        }
    }

    @Override
    public Types.StoreType getStoreType() {
        return storeType;
    }

}
