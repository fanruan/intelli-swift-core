package com.fr.swift.cube.space;

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

    public double of(long sizeOfBytes) {
        return ((double) sizeOfBytes) / (1 << offset);
    }
}