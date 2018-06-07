package com.fr.swift.segment;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.impl.mem.IntMemIo;
import com.fr.swift.cube.io.impl.mem.ObjectMemIo;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.BaseColumn;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anchore
 * @date 2018/6/6
 */
public class RealtimeColumn<V> extends BaseColumn<V> implements Column<V> {
    /**
     * value -> rows
     */
    private NavigableMap<V, MutableBitMap> valToRows;

    /**
     * index -> rows
     */
    private ObjectMemIo<MutableBitMap> indexToRows;

    /**
     * index -> value
     */
    private ObjectMemIo<V> indexToVal;

    /**
     * row -> index
     */
    private IntMemIo rowToIndex;


    RealtimeColumn(IResourceLocation location) {
        super(location);

        detailColumn = new RealtimeDetailColumn();
        dictColumn = new RealtimeDictColumn();
        indexColumn = new RealtimeBitmapColumn();
    }

    @Override
    public DictionaryEncodedColumn<V> getDictionaryEncodedColumn() {
        return dictColumn;
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return indexColumn;
    }

    @Override
    public DetailColumn<V> getDetailColumn() {
        return detailColumn;
    }

    private int findIndex(Object val) {
        return -1;
    }

    void prepareDict() {
        final AtomicInteger index = new AtomicInteger(0);
        for (Entry<V, MutableBitMap> entry : valToRows.entrySet()) {
            indexToVal.put(index.getAndIncrement(), entry.getKey());
            entry.getValue().traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    rowToIndex.put(row, index.get());
                }
            });
        }
    }

    class RealtimeDetailColumn implements DetailColumn<V> {
        @Override
        public int getInt(int pos) {
            return 0;
        }

        @Override
        public long getLong(int pos) {
            return 0;
        }

        @Override
        public double getDouble(int pos) {
            return 0;
        }

        @Override
        public void put(int row, V val) {
            if (!valToRows.containsKey(val)) {
                valToRows.put(val, BitMaps.newRoaringMutable());
            }
            valToRows.get(val).add(row);
        }

        @Override
        public V get(int pos) {
            return dictColumn.getValueByRow(pos);
        }

        @Override
        public void flush() {
        }

        @Override
        public void release() {
        }
    }

    class RealtimeDictColumn implements DictionaryEncodedColumn<V> {
        Comparator<V> c;

        @Override
        public int size() {
            return valToRows.size();
        }

        @Override
        public V getValue(int index) {
            return indexToVal.get(index);
        }

        @Override
        public V getValueByRow(int row) {
            return getValue(getIndexByRow(row));
        }

        @Override
        public int getIndex(Object value) {
            return findIndex(value);
        }

        @Override
        public int getIndexByRow(int row) {
            return rowToIndex.get(row);
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
            valToRows = null;
            rowToIndex.release();
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
            return indexToRows.get(index);
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
        }

        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
        }
    }
}