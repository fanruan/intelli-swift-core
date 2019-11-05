package com.fr.swift.structure.array;

import com.fr.swift.setting.PerformancePlugManager;

/**
 * @author 小灰灰
 * @date 2017/5/22
 */
public class IntListFactory {
    public static IntList createIntList() {
        return PerformancePlugManager.getInstance().isDirectGenerating() ? new DirectIntList() : new HeapIntList();
    }

    public static IntList createIntList(int capacity) {
        return PerformancePlugManager.getInstance().isDirectGenerating() ? new DirectIntList(capacity) : new HeapIntList(capacity);
    }

    public static HeapIntList createHeapIntList() {
        return new HeapIntList();
    }

    public static HeapIntList createHeapIntList(int capacity) {
        return new HeapIntList(capacity);
    }

    public static IntArray createIntArray(int capacity, int defaultValue) {
        return PerformancePlugManager.getInstance().isDirectGenerating() ? new DirectIntArray(capacity, defaultValue) : new HeapIntArray(capacity, defaultValue);
    }

    public static IntList createRangeIntList(int startIndex, int endIndexIncluded) {
        return new RangeIntList(startIndex, endIndexIncluded);
    }

    public static IntList createEmptyIntList() {
        return new EmptyIntList();
    }

    public static IntList newSingletonList(int onlyOne) {
        return createRangeIntList(onlyOne, onlyOne);
    }
}
