package com.fr.swift.segment.column;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.impl.mem.ObjectMemIo;
import com.fr.swift.cube.io.impl.mem.SwiftObjectMemIo;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.impl.BaseColumn;
import com.fr.third.guava.collect.BiMap;
import com.fr.third.guava.collect.HashBiMap;

import java.util.Comparator;
import java.util.NavigableMap;

/**
 * @author anchore
 * @date 2018/6/6
 */
abstract class BaseRealtimeColumn<V> extends BaseColumn<V> implements Column<V> {
    private static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    /**
     * row -> value
     */
    private ObjectMemIo<V> detail = new SwiftObjectMemIo<V>(1);

    Comparator<V> c;

    /**
     * value -> rows
     */
    NavigableMap<V, MutableBitMap> valToRows;

    /**
     * value <-> index
     */
    private BiMap<V, Integer> valAndIndex = HashBiMap.create();

    /**
     * 新插入的最小值
     */
    private V minAddedValue;

    private boolean hasAddedValue;

    BaseRealtimeColumn(IResourceLocation location) {
        super(location);
        init();
    }

    protected abstract DictionaryEncodedColumn.Type getType();

    /**
     * 刷新索引
     */
    private void refreshIfNeed() {
        if (!hasAddedValue) {
            return;
        }

        int newIndex = valToRows.size() - 1;

        // 从后往前更新偏移
        NavigableMap<V, MutableBitMap> descendingMap = valToRows.descendingMap();
        for (V v : descendingMap.headMap(minAddedValue, true).keySet()) {
            valAndIndex.put(v, newIndex--);
        }

        hasAddedValue = false;
    }

    private class RealtimeDetailColumn implements DetailColumn<V> {
        @Override
        public void put(int row, V val) {
            detail.put(row, val);

            if (valToRows.containsKey(val)) {
                valToRows.get(val).add(row);
                return;
            }

            MutableBitMap bitmap = BitMaps.newRoaringMutable();
            bitmap.add(row);
            valToRows.put(val, bitmap);

            // 更新新加的最小值
            if (hasAddedValue) {
                if (c.compare(minAddedValue, val) > 0) {
                    minAddedValue = val;
                }
            } else {
                minAddedValue = val;
            }

            if (!hasAddedValue) {
                hasAddedValue = true;
            }
        }

        @Override
        public V get(int pos) {
            return detail.get(pos);
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
            int size = valToRows.size();
            return valToRows.containsKey(null) ? size : size + 1;
        }

        @Override
        public V getValue(int index) {
            if (index < 1) {
                return null;
            }

            refreshIfNeed();

            int realIndex = valToRows.containsKey(null) ? index : index - 1;

            BiMap<Integer, V> indexToVal = valAndIndex.inverse();
            if (indexToVal.containsKey(realIndex)) {
                return indexToVal.get(realIndex);
            }
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override
        public int getIndex(Object value) {
            if (value == null) {
                return 0;
            }

            refreshIfNeed();

            if (valAndIndex.containsKey(value)) {
                int index = valAndIndex.get(value);
                return valToRows.containsKey(null) ? index : index + 1;
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
            valAndIndex.clear();
        }

        @Override
        public Comparator<V> getComparator() {
            return c;
        }

        @Override
        public Type getType() {
            return BaseRealtimeColumn.this.getType();
        }

        @Override
        public void flush() {
        }

        @Override
        public void putSize(int size) {
        }

        @Override
        public void putGlobalSize(int globalSize) {
        }

        @Override
        public void putValue(int index, V val) {
        }

        @Override
        public void putIndex(int row, int index) {
        }

        @Override
        public void putGlobalIndex(int index, int globalIndex) {
        }
    }

    class RealtimeBitmapColumn implements BitmapIndexedColumn {
        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
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
        }

        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
            V v = dictColumn.getValue(index);
            if (!bitmap.isEmpty()) {
                valToRows.put(v, (MutableBitMap) bitmap);
            } else {
                valToRows.remove(v);
                //todo 空字典清楚问题
//                Integer vIndex = valAndIndex.get(v);
//                BiMap<V, Integer> newValAndIndex = HashBiMap.create();
//                for (Map.Entry<V, Integer> vIntegerEntry : valAndIndex.entrySet()) {
//                    if (vIntegerEntry.getValue() < vIndex) {
//                        newValAndIndex.put(vIntegerEntry.getKey(), vIntegerEntry.getValue());
//                    } else if (vIntegerEntry.getValue() > vIndex) {
//                        newValAndIndex.put(vIntegerEntry.getKey(), vIntegerEntry.getValue() - 1);
//                    }
//                }
            }
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
            V v = dictColumn.getValue(0);
            MutableBitMap mutableBitMap = valToRows.containsKey(v) ? valToRows.get(v) : BitMaps.newRoaringMutable();
            mutableBitMap.and(bitMap);
        }
    }

    void init() {
        BuildConf readConf = new BuildConf(IoType.READ, DataType.REALTIME_COLUMN);
        if (!DISCOVERY.exists(location, readConf)) {
            // 三个视图，映射至内存数据
            detailColumn = new RealtimeDetailColumn();
            dictColumn = new RealtimeDictColumn();
            indexColumn = new RealtimeBitmapColumn();

            DISCOVERY.<ObjectMemIo<BaseRealtimeColumn<V>>>getWriter(location, new BuildConf(IoType.WRITE, DataType.REALTIME_COLUMN)).put(0, this);
            return;
        }

        ObjectMemIo<BaseRealtimeColumn<V>> selfMemIo = DISCOVERY.getReader(location, readConf);
        BaseRealtimeColumn<V> self = selfMemIo.get(0);
        detail = self.detail;
        c = self.c;
        valToRows = self.valToRows;
        valAndIndex = self.valAndIndex;
        minAddedValue = self.minAddedValue;
        hasAddedValue = self.hasAddedValue;

        detailColumn = self.detailColumn;
        dictColumn = self.dictColumn;
        indexColumn = self.indexColumn;
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
}