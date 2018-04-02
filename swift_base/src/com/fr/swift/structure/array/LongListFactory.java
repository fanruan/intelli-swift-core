package com.fr.swift.structure.array;


import com.fr.swift.setting.PerformancePlugManager;

/**
 * Created by 小灰灰 on 2017/5/22.
 */
public class LongListFactory {
    public static LongArray createLongArray(int capacity, long defaultValue) {
        return PerformancePlugManager.getInstance().isDirectGenerating() ? new DirectLongArray(capacity, defaultValue) : new HeapLongArray(capacity, defaultValue);
    }
}
