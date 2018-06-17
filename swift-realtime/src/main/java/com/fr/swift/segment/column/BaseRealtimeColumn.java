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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author anchore
 * @date 2018/6/6
 */
abstract class BaseRealtimeColumn<V> extends BaseColumn<V> implements Column<V> {
    private static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    /**
     * row -> value
     */
    private ObjectMemIo<V> detail = new SwiftObjectMemIo<V>();

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
     * 新插入的值
     */
    SortedSet<V> addedValues;

    BaseRealtimeColumn(IResourceLocation location) {
        super(location);
        init();
    }

    /**
     * 刷新索引
     */
    private void refreshIfNeed() {
        if (addedValues.isEmpty()) {
            return;
        }

        int offset = 0, count = 0;

        Iterator<V> cutInValueItr = addedValues.iterator();
        V currentCutInValue = cutInValueItr.next(), firstCutInValue = currentCutInValue;

        Set<Entry<V, MutableBitMap>> tailEntries = valToRows.descendingMap().headMap(firstCutInValue, true).entrySet();
        for (Entry<V, MutableBitMap> entry : tailEntries) {
            V v = entry.getKey();

            if (c.compare(v, currentCutInValue) == 0) {
                offset++;
                currentCutInValue = cutInValueItr.hasNext() ? cutInValueItr.next() : null;
            }
            if (valAndIndex.containsKey(v)) {
                valAndIndex.put(v, valAndIndex.get(v) + offset);
            } else {
                valAndIndex.put(v, valToRows.size() - 1 - count);
                count++;
            }
        }

        if (valAndIndex.size() < valToRows.size()) {
            count = 0;
            for (V v : valToRows.headMap(firstCutInValue).keySet()) {
                valAndIndex.put(v, count++);
            }
        }

        addedValues.clear();
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

            addedValues.add(val);
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
            if (detail != null) {
                detail.release();
            }
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
            throw new NoSuchElementException(value.toString());
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
            if (valAndIndex != null) {
                valAndIndex.clear();
            }
        }

        @Override
        public Comparator<V> getComparator() {
            return c;
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
            if (valToRows != null) {
                valToRows.clear();
            }
        }

        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
        }
    }

    void init() {
        BuildConf readConf = new BuildConf(IoType.READ, DataType.REALTIME_COLUMN);
        if (!DISCOVERY.exists(location, readConf)) {
            DISCOVERY.<ObjectMemIo<BaseRealtimeColumn<V>>>getWriter(location, new BuildConf(IoType.WRITE, DataType.REALTIME_COLUMN)).put(0, this);
            return;
        }

        ObjectMemIo<BaseRealtimeColumn<V>> selfMemIo = DISCOVERY.getReader(location, readConf);
        BaseRealtimeColumn<V> self = selfMemIo.get(0);
        detail = self.detail;
        c = self.c;
        valToRows = self.valToRows;
        valAndIndex = self.valAndIndex;
        addedValues = self.addedValues;

        // 三个视图，映射至内存数据
        detailColumn = new RealtimeDetailColumn();
        dictColumn = new RealtimeDictColumn();
        indexColumn = new RealtimeBitmapColumn();
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