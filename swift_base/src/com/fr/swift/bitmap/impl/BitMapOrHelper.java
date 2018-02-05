package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;

/**
 * Created by Lyon on 2017/11/29.
 */
public class BitMapOrHelper {

    private MutableRoaringBitmap roaringBitmap = new MutableRoaringBitmap();

    public void add(ImmutableBitMap bitmap){
        if (bitmap != null){
            roaringBitmap.naivelazyor(((AbstractBitMap)bitmap).getBitMap());
        }
    }

    public ImmutableBitMap compute() {
        roaringBitmap.repairAfterLazy();
        return RoaringImmutableBitMap.newInstance(roaringBitmap);
    }
}
