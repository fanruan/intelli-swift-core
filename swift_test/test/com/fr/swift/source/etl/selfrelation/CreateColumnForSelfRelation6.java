package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.Comparator;

/**
 * Created by Handsome on 2018/1/19 0019 11:47
 */
public class CreateColumnForSelfRelation6 {
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
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
    }

    private BitmapIndexedColumn createBitmapColumn() {
        final MutableBitMap[] bitMaps = new MutableBitMap[6];
        bitMaps[0] = BitSetMutableBitMap.newInstance();
        bitMaps[1] = BitSetMutableBitMap.newInstance();
        bitMaps[2] = BitSetMutableBitMap.newInstance();
        bitMaps[3] = BitSetMutableBitMap.newInstance();
        bitMaps[4] = BitSetMutableBitMap.newInstance();
        bitMaps[5] = BitSetMutableBitMap.newInstance();
        bitMaps[0].add(0);
        bitMaps[1].add(1);
        bitMaps[2].add(2);
        bitMaps[3].add(3);
        bitMaps[4].add(4);
        bitMaps[4].add(7);
        bitMaps[5].add(5);
        bitMaps[5].add(6);
        bitMaps[5].add(8);
        return new BitmapIndexedColumn() {
            @Override
            public void flush() {

            }

            @Override
            public void putBitMapIndex(int index, ImmutableBitMap bitmap) {

            }

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                if(index < bitMaps.length) {
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
        final String[] keys = {"11","22","33","1","6",""};
        final int[] index = {0,1,2,3,4,5,5,4,5};
        return new DictionaryEncodedColumn() {

            @Override
            public void flush() {

            }

            @Override
            public int getGlobalIndexByRow(int row) {
                return 0;
            }

            @Override
            public int getGlobalIndexByIndex(int index) {
                return 0;
            }

            @Override
            public int size() {
                return 6;
            }

            @Override
            public void putGlobalSize(int globalSize) {

            }

            @Override
            public int globalSize() {
                return 0;
            }

            @Override
            public void putSize(int size) {

            }

            @Override
            public Object getValue(int index) {
                return keys[index];
            }

            @Override
            public void putValue(int index, Object val) {

            }

            @Override
            public int getIndex(Object value) {
                return 0;
            }

            @Override
            public void putIndex(int row, int index) {

            }

            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }

            @Override
            public void putGlobalIndex(int index, int globalIndex) {

            }

            @Override
            public void release() {

            }

            @Override
            public Comparator getComparator() {
                return new Comparator() {

                    @Override
                    public int compare(Object o3, Object o4) {
                        String o1 = (String)o3;
                        String o2 = (String)o4;
                        return o1.compareTo(o2);
                    }
                };
            }
        };
    }
}

