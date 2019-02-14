package com.fr.swift.structure.array;


import com.fr.swift.setting.PerformancePlugManager;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/2
 */
public class LongListFactory {
    public static LongArray createLongArray(int capacity, long defaultValue) {
        return PerformancePlugManager.getInstance().isDirectGenerating() ? new DirectLongArray(capacity, defaultValue) : new HeapLongArray(capacity, defaultValue);
    }

    public static LongArray createEmptyLongArray() {
        return new HeapLongArray(0);
    }

    public static LongArray fromList(List<Long> list) {
        return new HeapLongArray(list);
    }
}
