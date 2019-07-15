package com.fr.swift.segment.column.impl.multi;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.impl.base.BaseBitmapColumn;
import com.fr.swift.util.IoUtil;

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
        ImmutableBitMap[] bitmaps = new ImmutableBitMap[indices.size()];
        for (int i = 0; i < localIndices.length; i++) {
            int localIndex = localIndices[i];
            bitmaps[i] = localIndex == -1 ?
                    BitMaps.EMPTY_IMMUTABLE :
                    indices.get(i).getBitMapIndex(localIndex);
        }
        return new MultiBitmap(bitmaps, offsets);
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