package com.fr.swift.query.group.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.Comparator;

public class SortByOtherFieldColumn implements Column {

    //根据的字段列
    private Column sortByColumn;
    //待排序的字段列
    private Column originColumn;
    private SortType sortType;

    public SortByOtherFieldColumn(Column sortByColumn, Column originColumn, SortType type) {
        this.sortByColumn = sortByColumn;
        this.originColumn = originColumn;
        this.sortType = type;
        //    initDescBitmap();
    }

    @Override
    public DictionaryEncodedColumn getDictionaryEncodedColumn() {
        return originColumn.getDictionaryEncodedColumn();
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new GroupBitmapColumn();
    }

    @Override
    public DetailColumn getDetailColumn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IResourceLocation getLocation() {
        return null;
    }

    private class GroupBitmapColumn implements BitmapIndexedColumn {

        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            if (sortType == SortType.DESC) {
                return sortByColumn.getBitmapIndex().getBitMapIndex(sortByColumn.getDictionaryEncodedColumn().size() - index - 1);
            }
            return sortByColumn.getBitmapIndex().getBitMapIndex(index);
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImmutableBitMap getNullIndex() {
            return sortByColumn.getBitmapIndex().getNullIndex();
        }

        @Override
        public void flush() {
        }

        @Override
        public void release() {
            throw new UnsupportedOperationException();
        }
    }

//    private class DicColumn implements DictionaryEncodedColumn {
//
//        DictionaryEncodedColumn originDict = originColumn.getDictionaryEncodedColumn();
//
//        @Override
//        public void putSize(int size) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public int size() {
//            return sortByColumn.getDictionaryEncodedColumn().size();
//        }
//
//        @Override
//        public void putGlobalSize(int globalSize) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public int globalSize() {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public void putValue(int index, Object val) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public Object getValue(int index) {
//            return originDict.getValue(index);
//        }
//
//        @Override
//        public int getIndex(Object value) {
//            return originDict.getIndex(value);
//        }
//
//        @Override
//        public void putIndex(int row, int index) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public int getIndexByRow(int row) {
//            return originDict.getIndexByRow(row);
//        }
//
//        @Override
//        public void putGlobalIndex(int index, int globalIndex) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public int getGlobalIndexByIndex(int index) {
//            return originDict.getGlobalIndexByIndex(index);
//        }
//
//        @Override
//        public int getGlobalIndexByRow(int row) {
//            return originDict.getGlobalIndexByRow(row);
//        }
//
//        @Override
//        public Comparator getComparator() {
//            return originDict.getComparator();
//        }
//
//        @Override
//        public Object convertValue(Object value) {
//            return originDict.convertValue(value);
//        }
//
//        @Override
//        public void flush() {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public void release() {
//            throw new UnsupportedOperationException();
//        }
//    }

}
