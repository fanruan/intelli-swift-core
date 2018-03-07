package com.fr.swift.query.group.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/1/29
 * <p>
 * 新分组用String表示分组值
 */
class GroupColumn implements Column<String> {
    private GroupRule groupRule;
    private ImmutableBitMap[] groupedBitmaps;

    GroupColumn(BitmapIndexedColumn indexColumn, GroupRule groupRule) {
        this.groupRule = groupRule;
        group(indexColumn);
    }

    private void group(BitmapIndexedColumn indexColumn) {
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
    public DictionaryEncodedColumn<String> getDictionaryEncodedColumn() {
        return new GroupDictColumn();
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new GroupBitmapColumn();
    }

    @Override
    public DetailColumn<String> getDetailColumn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IResourceLocation getLocation() {
        return null;
    }

    private class GroupDictColumn implements DictionaryEncodedColumn<String> {
        @Override
        public void putSize(int size) {
        }

        @Override
        public int size() {
            return groupRule.newSize();
        }

        @Override
        public void putGlobalSize(int globalSize) {
        }

        @Override
        public int globalSize() {
            return 0;
        }

        @Override
        public void putValue(int index, String val) {
        }

        @Override
        public String getValue(int index) {
            return groupRule.getGroupName(index);
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
            return 0;
        }

        @Override
        public void putGlobalIndex(int index, int globalIndex) {
        }

        @Override
        public int getGlobalIndexByIndex(int index) {
            return 0;
        }

        @Override
        public int getGlobalIndexByRow(int row) {
            return 0;
        }

        @Override
        public Comparator<String> getComparator() {
            return null;
        }

        @Override
        public void flush() {
        }

        @Override
        public void release() {
        }
    }

    private class GroupBitmapColumn implements BitmapIndexedColumn {
        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
        }

        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            // 通过新分组号拿新索引
            return groupedBitmaps[index];
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
        }

        @Override
        public ImmutableBitMap getNullIndex() {
            return null;
        }

        @Override
        public void flush() {
        }

        @Override
        public void release() {
            groupedBitmaps = null;
        }
    }
}