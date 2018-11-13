package com.fr.swift.source.etl.rowcal.rank;

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
import com.fr.swift.test.Temps.TempDictColumn;

import java.util.Comparator;

/**
 * Created by Handsome on 2018/3/4 0004 15:04
 */
public class BaseCreateColumn3Test {
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
            int[] a = new int[]{10, 15, 16, 17, 11, 12, 18};

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
        final MutableBitMap[] bitMaps = new MutableBitMap[7];
        bitMaps[0] = BitSetMutableBitMap.newInstance();
        bitMaps[1] = BitSetMutableBitMap.newInstance();
        bitMaps[2] = BitSetMutableBitMap.newInstance();
        bitMaps[3] = BitSetMutableBitMap.newInstance();
        bitMaps[4] = BitSetMutableBitMap.newInstance();
        bitMaps[5] = BitSetMutableBitMap.newInstance();
        bitMaps[6] = BitSetMutableBitMap.newInstance();
        bitMaps[0].add(0);
        bitMaps[1].add(4);
        bitMaps[2].add(5);
        bitMaps[3].add(1);
        bitMaps[4].add(2);
        bitMaps[5].add(3);
        bitMaps[6].add(6);
        return new BitmapIndexedColumn() {
            @Override
            public boolean isReadable() {
                return false;
            }

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
        final int[] keys = {10, 11, 12, 15, 16, 17, 18};
        final int[] index = {0, 3, 4, 5, 1, 2, 6};
        return new TempDictColumn() {

            @Override
            public int size() {
                return 7;
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
                return new Comparator() {

                    @Override
                    public int compare(Object o3, Object o4) {
                        Integer o1 = (Integer) o3;
                        Integer o2 = (Integer) o4;
                        return o1.compareTo(o2);
                    }
                };
            }
        };
    }
}
