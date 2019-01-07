package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.cube.io.DoubleIo;

import java.util.Arrays;

/**
 * @author anchore
 * @date 2017/11/23
 */
public class DoubleMemIo extends BaseMemIo implements DoubleIo {
    private double[] mem;

    public DoubleMemIo() {
        this(DEFAULT_CAPACITY);
    }

    public DoubleMemIo(int cap) {
        mem = new double[cap];
    }

    @Override
    public double get(long pos) {
        checkPosition(pos);
        return mem[(int) pos];
    }

    @Override
    public boolean isReadable() {
        return mem != null && lastPos > -1;
    }

    @Override
    void ensureCapacity(long pos) {
        if (pos < mem.length) {
            return;
        }
        if (pos > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException(String.format("pos >= MAX_ARRAY_SIZE, pos: %d", pos));
        }
        mem = Arrays.copyOf(mem, expand(mem.length, pos));
    }

    @Override
    public void put(long pos, double val) {
        ensureCapacity(pos);
        mem[(int) pos] = val;
        if (lastPos < pos) {
            lastPos = pos;
        }
    }

    @Override
    public void release() {
        mem = null;
    }
}
