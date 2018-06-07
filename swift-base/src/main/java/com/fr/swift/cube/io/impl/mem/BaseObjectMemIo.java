package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.util.Crasher;

import java.util.Arrays;

/**
 * @author anchore
 * @date 2017/11/23
 */
abstract class BaseObjectMemIo<T> extends BaseMemIo implements ObjectMemIo<T> {
    T[] mem;

    @Override
    public T get(long pos) {
        checkPosition(pos);
        return mem[(int) pos];
    }

    @Override
    public long getLastPosition(long pos) {
        return lastPos;
    }

    @Override
    public boolean isReadable() {
        return mem != null;
    }

    @Override
    void ensureCapacity(long pos) {
        if (pos < mem.length) {
            return;
        }
        if (pos > Integer.MAX_VALUE) {
            Crasher.crash(String.format("pos > Integer.MAX_VALUE, pos: %d", pos));
            return;
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