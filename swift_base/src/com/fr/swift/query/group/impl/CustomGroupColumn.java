package com.fr.swift.query.group.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.group.CustomGroupRule;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/1/29
 */
class CustomGroupColumn<Base, Derive> implements Column<Derive> {
    private CustomGroupRule<Base, Derive> groupRule;

    private ImmutableBitMap[] groupedBitmaps;

    private Column<Base> originColumn;

    CustomGroupColumn(Column<Base> originColumn, CustomGroupRule<Base, Derive> groupRule) {
        this.originColumn = originColumn;
        this.groupRule = groupRule;
        group();
    }

    private void group() {
        groupRule.setOriginDict(originColumn.getDictionaryEncodedColumn());

        BitmapIndexedColumn indexColumn = originColumn.getBitmapIndex();
        int newSize = groupRule.newSize();
        groupedBitmaps = new ImmutableBitMap[newSize];
        for (int i = 0; i < newSize; i++) {
            IntList newGroup = groupRule.map(i);
            BitMapOrHelper orHelper = new BitMapOrHelper();
            for (int j = 0; j < newGroup.size(); j++) {
                orHelper.add(indexColumn.getBitMapIndex(newGroup.get(j)));
            }
            groupedBitmaps[i] = orHelper.compute();
        }
    }

    @Override
    public DictionaryEncodedColumn<Derive> getDictionaryEncodedColumn() {
        return new GroupDictColumn();
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new GroupBitmapColumn();
    }

    @Override
    public DetailColumn<Derive> getDetailColumn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IResourceLocation getLocation() {
        return null;
    }

    private class GroupDictColumn implements DictionaryEncodedColumn<Derive> {
        DictionaryEncodedColumn<Base> originDict = originColumn.getDictionaryEncodedColumn();

        @Override
        public void putSize(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return groupRule.newSize();
        }

        @Override
        public void putGlobalSize(int globalSize) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int globalSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putValue(int index, Derive val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Derive getValue(int index) {
            return groupRule.getValue(index);
        }

        @Override
        public int getIndex(Object value) {
            return groupRule.getIndex(value);
        }

        @Override
        public void putIndex(int row, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getIndexByRow(int row) {
            int originIndex = originDict.getIndexByRow(row);
            return groupRule.reverseMap(originIndex);
        }

        @Override
        public void putGlobalIndex(int index, int globalIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getGlobalIndexByIndex(int index) {
            return groupRule.getGlobalIndexByIndex(index);
        }

        @Override
        public int getGlobalIndexByRow(int row) {
            return getGlobalIndexByIndex(getIndexByRow(row));
        }

        @Override
        public Comparator<Derive> getComparator() {
            return new Comparator<Derive>() {
                @Override
                public int compare(Derive o1, Derive o2) {
                    return getIndex(o1) - getIndex(o2);
                }
            };
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() {
            throw new UnsupportedOperationException();
        }
    }

    private class GroupBitmapColumn implements BitmapIndexedColumn {
        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            // 通过新分组号拿新索引
            return groupedBitmaps[index];
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImmutableBitMap getNullIndex() {
            return getBitMapIndex(0);
        }

        @Override
        public void release() {
            groupedBitmaps = null;
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException();
        }
    }
}