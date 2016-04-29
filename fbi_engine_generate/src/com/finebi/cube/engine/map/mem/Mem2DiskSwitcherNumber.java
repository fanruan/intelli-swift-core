package com.finebi.cube.engine.map.mem;

import com.finebi.cube.engine.map.ExternalMap;

import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/16.
 */
public class Mem2DiskSwitcherNumber<K, V> extends Mem2DiskSwitcher<K, V> {
    private int threshold;

    @Override
    long getThreshold() {
        return threshold;
    }

    public Mem2DiskSwitcherNumber(TreeMap mem, ExternalMap disk, int threshold) {
        super(mem, disk);
        this.threshold = threshold;
    }
}