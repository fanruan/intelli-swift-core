package com.fr.bi.stable.structure.array;

import com.fr.bi.manager.PerformancePlugManager;

/**
 * Created by 小灰灰 on 2017/5/22.
 */
public class IntListFactory {
    public static IntList createIntList(){
        return PerformancePlugManager.getInstance().isDirectGenerating() ? new DirectIntList() : new HeapIntList();
    }

    public static IntList createIntList(int capacity){
        return PerformancePlugManager.getInstance().isDirectGenerating() ? new DirectIntList(capacity) : new HeapIntList(capacity);
    }

    public static IntArray createIntArray(int capacity, int defaultValue){
        return PerformancePlugManager.getInstance().isDirectGenerating() ? new DirectIntArray(capacity, defaultValue) : new HeapIntArray(capacity, defaultValue);
    }
}
