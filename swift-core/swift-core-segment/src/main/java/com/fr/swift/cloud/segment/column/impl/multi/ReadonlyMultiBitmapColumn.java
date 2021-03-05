package com.fr.swift.cloud.segment.column.impl.multi;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.impl.FasterAggregation;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.impl.base.BaseBitmapColumn;
import com.fr.swift.cloud.util.IoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2019/7/11
 */
class ReadonlyMultiBitmapColumn extends BaseBitmapColumn {
    private List<BitmapIndexedColumn> indices;

    private int[] offsets;

    private ReadonlyMultiDictColumn<?> globalDict;

    public ReadonlyMultiBitmapColumn(List<BitmapIndexedColumn> indices, int[] offsets, ReadonlyMultiDictColumn<?> globalDict) {
        this.indices = indices;
        this.offsets = offsets;
        this.globalDict = globalDict;
    }

    @Override
    public ImmutableBitMap getBitMapIndex(int index) {
        int[] localIndices = globalDict.getLocalIndices(index);
        List<ImmutableBitMap> bitmaps = new ArrayList<ImmutableBitMap>();
        for (int i = 0; i < localIndices.length; i++) {
            int localIndex = localIndices[i];
            bitmaps.add(localIndex == -1 ?
                    BitMaps.EMPTY_IMMUTABLE :
                    indices.get(i).getBitMapIndex(localIndex));
        }
        return FasterAggregation.compose(bitmaps, offsets);
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public void release() {
        IoUtil.release(indices.toArray(new BitmapIndexedColumn[0]));
    }

    @Override
    public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
        throw new UnsupportedOperationException();
    }
}