package com.fr.swift.cube.space;

/**
 * @author anchore
 * @date 2018/4/14
 */
public interface SpaceUsage {
    /**
     * 已占用
     *
     * @return MB
     */
    double getUsed();

    /**
     * 剩余
     *
     * @return MB
     */
    double getUsable();

    /**
     * 总容量
     *
     * @return MB
     */
    double getCapacity();

    enum SpaceUnit {
        B(0), KB(10), MB(20), GB(30), TB(40);

        private final int offset;

        SpaceUnit(int offset) {
            this.offset = offset;
        }

        public double of(long sizeOfBytes) {
            return ((double) sizeOfBytes) / (1 << offset);
        }
    }
}