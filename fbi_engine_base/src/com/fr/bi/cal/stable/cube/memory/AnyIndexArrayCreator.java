package com.fr.bi.cal.stable.cube.memory;

import com.fr.bi.manager.PerformancePlugManager;

/**
 * Created by 小灰灰 on 2017/6/9.
 */
public class AnyIndexArrayCreator<T> {
    public AnyIndexArray<T> create(){
        int size = PerformancePlugManager.getInstance().getMaxSPADetailSize();
        return size == 0 ? new AnyIndexArray<T>() : new DataLimitAnyIndexArray<T>(size);
    }
}
