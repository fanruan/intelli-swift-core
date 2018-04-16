package com.fr.swift.cube.space;

/**
 * @author anchore
 * @date 2018/4/14
 */
public interface SpaceUsage {
    /**
     * 已占用
     *
     * @return bytes
     */
    long getUsed();

    /**
     * 剩余
     *
     * @return bytes
     */
    long getUsable();

    /**
     * 总容量
     *
     * @return bytes
     */
    long getTotal();

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