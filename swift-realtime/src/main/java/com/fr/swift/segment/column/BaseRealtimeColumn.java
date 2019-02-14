package com.fr.swift.segment.column;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.impl.mem.ObjectMemIo;
import com.fr.swift.cube.io.impl.mem.SwiftObjectMemIo;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.impl.BaseColumn;
import com.fr.swift.segment.column.impl.base.IResourceDiscovery;
import com.fr.swift.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.util.Optional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author anchore
 * @date 2018/6/6
 */
abstract class BaseRealtimeColumn<V> extends BaseColumn<V> implements Column<V> {
    private static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    /**
     * row -> value
     */
    private ObjectMemIo<V> detail;

    /**
     * value -> rows
     */
    private ConcurrentMap<V, MutableBitMap> valToRows;

    private MutableBitMap nullIndex;

    private Optional<Integer> nullId;

    /**
     * index <-> id
     */
    private IndexAndId indexAndId;

    /**
     * id -> value
     */
    private List<V> idToVal;

    /**
     * value -> id
     */
    private ConcurrentNavigableMap<V, Integer> valToId;

    private class RealtimeDetailColumn implements DetailColumn<V> {
        @Override
        public void put(int row, V val) {
            detail.put(row, val);

            if (val == null) {
                if (!nullId.isPresent()) {
                    nullId = Optional.of(idToVal.size());
                    idToVal.add(null);
                }
                nullIndex.add(row);
                return;
            }

            if (valToRows.containsKey(val)) {
                valToRows.get(val).add(row);
                return;
            }

            valToId.put(val, idToVal.size());

            idToVal.add(val);

            MutableBitMap bitmap = BitMaps.newRoaringMutable();
            bitmap.add(row);
            valToRows.put(val, bitmap);
        }

        @Override
        public V get(int pos) {
            return detail.get(pos);
        }

        @Override
        public boolean isReadable() {
            return detail != null && detail.isReadable();
        }

        @Override
        public int getInt(int pos) {
            return (Integer) get(pos);
        }

        @Override
        public long getLong(int pos) {
            return (Long) get(pos);
        }

        @Override
        public double getDouble(int pos) {
            return (Double) get(pos);
        }

        @Override
        public void flush() {
        }

        @Override
        public void release() {
            detail.release();
        }
    }

    private class RealtimeDictColumn implements DictionaryEncodedColumn<V> {
        @Override
        public int size() {
            return nullId.isPresent() ? indexAndId.size() : indexAndId.size() + 1;
        }

        @Override
        public V getValue(int index) {
            if (index < 1) {
                return null;
            }

            int id = indexAndId.getId(nullId.isPresent() ? index : index - 1);
            return idToVal.get(id);
        }

        @Override
        public int getIndex(Object value) {
            if (value == null) {
                return 0;
            }

            if (valToId.containsKey(value)) {
                int index = indexAndId.getIndex(valToId.get(value));
                return nullId.isPresent() ? index : index + 1;
            }
            return -1;
        }

        @Override
        public int getIndexByRow(int row) {
            return getIndex(detail.get(row));
        }

        @Override
        public V getValueByRow(int row) {
            return detail.get(row);
        }

        @Override
        public int getGlobalIndexByIndex(int index) {
            return index;
        }

        @Override
        public int globalSize() {
            return size();
        }

        @Override
        public int getGlobalIndexByRow(int row) {
            return getGlobalIndexByIndex(getIndexByRow(row));
        }

        @Override
        public void release() {
            idToVal.clear();
            valToId.clear();
            indexAndId.release();
        }

        @Override
        public Comparator<V> getComparator() {
            return (Comparator<V>) valToId.comparator();
        }

        @Override
        public ColumnTypeConstants.ClassType getType() {
            return BaseRealtimeColumn.this.getType();
        }

        @Override
        public Putter<V> putter() {
            throw new IllegalStateException("real time dict column needn't put operation");
        }

        @Override
        public void flush() {
        }

        @Override
        public boolean isReadable() {
            return detail != null && detail.isReadable();
        }
    }

    private class RealtimeBitmapColumn implements BitmapIndexedColumn {
        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            if (index < 1) {
                return nullIndex;
            }
            V v = dictColumn.getValue(index);
            return valToRows.containsKey(v) ? valToRows.get(v) : BitMaps.newRoaringMutable();
        }

        @Override
        public ImmutableBitMap getNullIndex() {
            return getBitMapIndex(0);
        }

        @Override
        public void flush() {
        }

        @Override
        public void release() {
            valToRows.clear();
            nullIndex = null;
            nullId = null;
        }

        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
            MutableBitMap mBitmap = (MutableBitMap) bitmap;
            if (index < 1) {
                nullIndex = mBitmap;
                return;
            }

            V v = dictColumn.getValue(index);
            if (bitmap.isEmpty()) {
                valToRows.remove(v);
            } else {
                valToRows.put(v, mBitmap);
            }
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
            putBitMapIndex(0, bitMap);
        }

        @Override
        public boolean isReadable() {
            return valToRows != null && nullIndex != null;
        }
    }

    BaseRealtimeColumn(IResourceLocation location) {
        super(location);
        init();
    }

    private void init() {
        BuildConf readConf = new BuildConf(IoType.READ, DataType.REALTIME_COLUMN);
        if (!DISCOVERY.exists(location, readConf)) {
            synchronized (DISCOVERY) {
                if (!DISCOVERY.exists(location, readConf)) {
                    detail = new SwiftObjectMemIo<V>(1);
                    valToRows = new ConcurrentHashMap<V, MutableBitMap>();
                    valToId = new ConcurrentSkipListMap<V, Integer>(getComparator());
                    idToVal = new ArrayList<V>();
                    indexAndId = new IndexAndId();

                    nullIndex = BitMaps.newRoaringMutable();
                    nullId = Optional.empty();

                    // 三个视图，映射至内存数据
                    detailColumn = new RealtimeDetailColumn();
                    dictColumn = new RealtimeDictColumn();
                    indexColumn = new RealtimeBitmapColumn();

                    DISCOVERY.<ObjectMemIo<BaseRealtimeColumn<V>>>getWriter(location, new BuildConf(IoType.WRITE, DataType.REALTIME_COLUMN)).put(0, this);
                }
            }
        }

        ObjectMemIo<BaseRealtimeColumn<V>> selfMemIo = DISCOVERY.getReader(location, readConf);
        BaseRealtimeColumn<V> self = selfMemIo.get(0);
        self.snapshot();
        // 还原对象
        detail = self.detail;
        valToRows = self.valToRows;
        valToId = self.valToId;
        idToVal = self.idToVal;
        indexAndId = self.indexAndId;

        nullIndex = self.nullIndex;
        nullId = self.nullId;

        detailColumn = self.detailColumn;
        dictColumn = self.dictColumn;
        indexColumn = self.indexColumn;
    }

    private void snapshot() {
        int lastId = idToVal.size() - 1;
        if (lastId < indexAndId.size()) {
            return;
        }

        synchronized (this) {
            if (lastId < indexAndId.size()) {
                return;
            }

            int newIndex = 0;
            IndexAndId indexAndId = new IndexAndId(lastId + 1);
            if (nullId.isPresent() && nullId.get() <= lastId) {
                indexAndId.putIndexAndId(nullId.get(), newIndex++);
            }
            for (Integer id : valToId.values()) {
                if (id <= lastId) {
                    indexAndId.putIndexAndId(id, newIndex++);
                }
            }
            this.indexAndId = indexAndId;
        }
    }

    @Override
    public DetailColumn<V> getDetailColumn() {
        return detailColumn;
    }

    @Override
    public DictionaryEncodedColumn<V> getDictionaryEncodedColumn() {
        return dictColumn;
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return indexColumn;
    }

    protected abstract Comparator<V> getComparator();

    protected abstract ColumnTypeConstants.ClassType getType();
}