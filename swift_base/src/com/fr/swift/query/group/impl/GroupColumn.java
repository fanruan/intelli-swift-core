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
import com.fr.swift.util.Extends.ExtendsBitmapColumn;
import com.fr.swift.util.Extends.ExtendsDictColumn;

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

    private class GroupDictColumn extends ExtendsDictColumn<String> {
        @Override
        public int size() {
            return groupRule.newSize();
        }

        @Override
        public String getValue(int index) {
            return groupRule.getGroupName(index);
        }
    }

    private class GroupBitmapColumn extends ExtendsBitmapColumn {
        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            // 通过新分组号拿新索引
            return groupedBitmaps[index];
        }

        @Override
        public void release() {
            groupedBitmaps = null;
        }
    }
}