package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.test.Temps;
import com.fr.swift.test.Temps.TempDictColumn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Lyon on 2017/12/1.
 */
public class BaseColumnImplTest<T> implements Column {

    private List<T> keys;
    private TreeMap<T, MutableBitMap> val2Rows;
    private MutableBitMap nullRows = BitMaps.newRoaringMutable();
    private Comparator<T> comparator;
    private List<T> details;

    public BaseColumnImplTest(List<T> details, Comparator<T> comparator) {
        this.details = details;
        this.comparator = comparator;
        this.val2Rows = new TreeMap<T, MutableBitMap>(comparator);
        this.keys = new ArrayList<T>();
        keys.add(null);
        prepare(details);
    }

    private void prepare(List<T> details) {
        for (int i = 0; i < details.size(); i++) {
            T val = details.get(i);
            if (val == null) {
                nullRows.add(i);
            } else {
                MutableBitMap bitMap = val2Rows.get(val);
                if (bitMap == null) {
                    bitMap = BitMaps.newRoaringMutable();
                    val2Rows.put(val, bitMap);
                }
                bitMap.add(i);
            }
        }
        keys.addAll(val2Rows.keySet());
    }

    @Override
    public DictionaryEncodedColumn getDictionaryEncodedColumn() {
        return new TempDictColumn() {
            @Override
            public int size() {
                return val2Rows.size() + 1;
            }

            @Override
            public Object getValue(int index) {
                return keys.get(index);
            }

            @Override
            public int getIndex(Object value) {
                return value == null ? 0 : keys.indexOf(value);
            }

            @Override
            public int getIndexByRow(int row) {
                return getIndex(details.get(row));
            }

            @Override
            public Comparator getComparator() {
                return comparator;
            }
        };
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new Temps.TempBitmapIndexedColumn() {
            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                return index == 0 ? nullRows : val2Rows.get(keys.get(index));
            }

            @Override
            public ImmutableBitMap getNullIndex() {
                return nullRows;
            }
        };
    }

    @Override
    public DetailColumn getDetailColumn() {
        return new Temps.TempDetailColumn() {
            @Override
            public Object get(int pos) {
                return details.get(pos);
            }
        };
    }

    @Override
    public IResourceLocation getLocation() {
        return null;
    }
}
