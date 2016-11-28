package com.fr.bi.stable.gvi;

import com.fr.bi.stable.gvi.roaringbitmap.RoaringBitmap;

/**
 * Created by 小灰灰 on 2016/11/24.
 */
public class GroupValueIndexOrHelper {
    private RoaringBitmap roaringBitmap = new RoaringBitmap();

    public void add(GroupValueIndex bitmap){
        if (bitmap != null){
            roaringBitmap.or(((AbstractGroupValueIndex)bitmap).getBitMap());
        }
    }

    public RoaringGroupValueIndex compute(){
        //roaringBitmap.repairAfterLazy();
        return new RoaringGroupValueIndex(roaringBitmap);
    }
}
