package com.fr.swift.space;

/**
 * @author anchore
 * @date 2018/4/17
 */
public enum SpaceUnit {
    // 单位
    B(0), KB(10), MB(20), GB(30), TB(40);

    private final int offset;

    SpaceUnit(int offset) {
        this.offset = offset;
    }

    public double ofBytes(long sizeInBytes) {
        return sizeInBytes / (double) (1L << offset);
    }

    public double toBytes(double sizeInUnits) {
        return sizeInUnits * (1L << offset);
    }
}