package com.fr.swift.source.etl;

import com.fr.swift.Temps.TempDictColumn;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;

import java.util.Comparator;

/**
 * Created by Handsome on 2017/12/28 0028 16:19
 */
public class CreateColumnForSum {
    public Column getColumn() {
        return new Column() {

            @Override
            public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                return createDicColumn();
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return createBitmapColumn();
            }

            @Override
            public DetailColumn getDetailColumn() {
                return createPrimitiveDetailColumn(new ResourceLocation("C:/aaa"));
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
    }

    private IntDetailColumn createPrimitiveDetailColumn(ResourceLocation parent) {
        return new IntDetailColumn(parent) {
            int[] a = new int[]{5, 1, 2, 2, 2, 1, 1, 5, 2};

            @Override
            public int getInt(int index) {
                return a[index];
            }

            @Override
            public void release() {

            }
        };
    }

    private BitmapIndexedColumn createBitmapColumn() {
        final MutableBitMap[] bitMaps = new MutableBitMap[3];
        bitMaps[0] = BitSetMutableBitMap.newInstance();
        bitMaps[1] = BitSetMutableBitMap.newInstance();
        bitMaps[2] = BitSetMutableBitMap.newInstance();
        bitMaps[0].add(1);
        bitMaps[0].add(5);
        bitMaps[0].add(6);
        bitMaps[1].add(2);
        bitMaps[1].add(3);
        bitMaps[1].add(4);
        bitMaps[1].add(8);
        bitMaps[2].add(0);
        bitMaps[2].add(7);
        return new BitmapIndexedColumn() {
            @Override
            public void flush() {

            }

            @Override
            public void putBitMapIndex(int index, ImmutableBitMap bitmap) {

            }

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                if (index < bitMaps.length) {
                    return bitMaps[index];
                }
                return null;
            }

            @Override
            public void putNullIndex(ImmutableBitMap bitMap) {

            }

            @Override
            public ImmutableBitMap getNullIndex() {
                return null;
            }

            @Override
            public void release() {

            }
        };

    }

    private DictionaryEncodedColumn createDicColumn() {
        final long[] keys = {1, 5, 2};
        final int[] index = {1, 0, 2, 2, 2, 0, 0, 1, 2};
        return new TempDictColumn() {

            @Override
            public int size() {
                return 3;
            }

            @Override
            public Object getValue(int index) {
                return keys[index];
            }

            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }

            @Override
            public Comparator getComparator() {
                return null;
            }
        };
    }
}
