package com.fr.swift.query.group.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

public class SortByOtherFieldColumn implements Column {

    private Column sortByColumn;
    private SortType sortType;

    public SortByOtherFieldColumn(Column sortByColumn, SortType type) {
        this.sortByColumn = sortByColumn;
        this.sortType = type;
        //    initDescBitmap();
    }

    @Override
    public DictionaryEncodedColumn getDictionaryEncodedColumn() {
        throw new UnsupportedOperationException();
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
}
