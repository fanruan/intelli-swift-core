package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.util.ArrayLookupHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/1.
 */
public abstract class ColumnImplTest<T> implements Column {

    private DictionaryEncodedColumn<T> dict;
    protected List<T> groups;
    private TreeMap<T, MutableBitMap> indexes = new TreeMap<>();
    protected Comparator<T> comparator;
    private T nullKey;

    public ColumnImplTest(List<T> details, Comparator<T> comparator, T nullKey) {
        this.comparator = comparator;
        this.nullKey = nullKey;
        init(details);
    }

    protected abstract T convertValue(Object value);

    private void init(List<T> list) {
        list.stream().forEach(s -> indexes.putIfAbsent(s, BitMaps.newRoaringMutable()));
        IntStream.range(0, list.size()).forEach(i -> indexes.get(list.get(i)).add(i));
        groups = new ArrayList<>();
        groups.addAll(indexes.keySet());
        dict = new DictionaryEncodedColumn<T>() {

            private ArrayLookupHelper.Lookup<T> lookup = new ArrayLookupHelper.Lookup<T>() {
                @Override
                public int minIndex() {
                    return 0;
                }

                @Override
                public int maxIndex() {
                    return size() - 1;
                }

                @Override
                public T lookupByIndex(int index) {
                    return getValue(index);
                }

                @Override
                public int compare(T t1, T t2) {
                    return comparator.compare(t1, t2);
                }

            };

            @Override
            public void flush() {

            }

            @Override
            public int size() {
                return groups.size();
            }

            @Override
            public void putGlobalSize(int globalSize) {

            }

            @Override
            public int globalSize() {
                return size();
            }

            @Override
            public void putSize(int size) {
                throw new UnsupportedOperationException();
            }

            @Override
            public T getValue(int index) {
                return groups.get(index);
            }

            @Override
            public void putValue(int index, T val) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getIndex(Object value) {
                return ArrayLookupHelper.lookup((T[]) new Object[]{ColumnImplTest.this.convertValue(value)}, lookup)[0];
            }

            @Override
            public void putIndex(int row, int index) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getIndexByRow(int row) {
                return 0;
            }

            @Override
            public void putGlobalIndex(int index, int globalIndex) {

            }

            @Override
            public int getGlobalIndexByRow(int row) {
                return 0;
            }

            @Override
            public int getGlobalIndexByIndex(int index) {
                return index;
            }

            @Override
            public Comparator<T> getComparator() {
                return comparator;
            }

            @Override
            public T convertValue(Object value) {
                return ColumnImplTest.this.convertValue(value);
            }

            @Override
            public void release() {

            }
        };
    }

    public List<T> getGroups() {
        return groups;
    }

    @Override
    public DictionaryEncodedColumn getDictionaryEncodedColumn() {
        return dict;
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new BitmapIndexedColumn() {
            @Override
            public void flush() {

            }

            @Override
            public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                return indexes.get(groups.get(index));
            }

            @Override
            public void putNullIndex(ImmutableBitMap bitMap) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ImmutableBitMap getNullIndex() {
                return indexes.get(nullKey);
            }

            @Override
            public void release() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public DetailColumn getDetailColumn() {
        return null;
    }

    @Override
    public IResourceLocation getLocation() {
        return null;
    }
}
