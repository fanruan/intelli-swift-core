package com.fr.swift.query.group.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.FasterAggregation;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.group.CustomGroupRule;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
            List<ImmutableBitMap> bitmaps = new ArrayList<ImmutableBitMap>();
            for (int j = 0; j < newGroup.size(); j++) {
                bitmaps.add(indexColumn.getBitMapIndex(newGroup.get(j)));
            }
            groupedBitmaps[i] = FasterAggregation.or(bitmaps);
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
        public int size() {
            return groupRule.newSize();
        }

        @Override
        public int globalSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Derive getValue(int index) {
            return groupRule.getValue(index);
        }

        @Override
        public Derive getValueByRow(int row) {
            return getValue(getIndexByRow(row));
        }

        @Override
        public int getIndex(Object value) {
            return groupRule.getIndex(value);
        }

        @Override
        public int getIndexByRow(int row) {
            int originIndex = originDict.getIndexByRow(row);
            // fixme 一个值可以在多个分组 nb
            return groupRule.reverseMap(originIndex).get(0);
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
        public ColumnTypeConstants.ClassType getType() {
            return originDict.getType();
        }

        @Override
        public Putter<Derive> putter() {
            return null;
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReadable() {
            return originDict.isReadable();
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

        @Override
        public boolean isReadable() {
            return !Util.isEmpty(groupedBitmaps);
        }
    }
}