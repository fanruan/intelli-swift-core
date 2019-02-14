package com.fr.swift.cube.io.impl.mem;

import java.util.Arrays;

/**
 * @author anchore
 * @date 2017/11/23
 */
public class SwiftObjectMemIo<T> extends BaseMemIo implements ObjectMemIo<T> {
    private Object[] mem;

    public SwiftObjectMemIo() {
        this(DEFAULT_CAPACITY);
    }

    public SwiftObjectMemIo(int cap) {
        mem = new Object[cap];
    }

    @Override
    public T get(long pos) {
        checkPosition(pos);
        return (T) mem[(int) pos];
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
    public void put(long pos, T val) {
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