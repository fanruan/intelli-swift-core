package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.io.IntIo;
import com.fr.swift.util.Crasher;

import java.util.Arrays;

/**
 * @author anchore
 * @date 2017/11/23
 */
public class IntMemIo extends BaseMemIo implements IntIo, Cloneable {
    private int[] mem;

    public IntMemIo() {
        this(DEFAULT_CAPACITY);
    }

    public IntMemIo(int cap) {
        mem = new int[cap];
    }

    @Override
    public int get(long pos) {
        checkPosition(pos);
        return mem[(int) pos];
    }

    @Override
    public boolean isReadable() {
        return mem != null && lastPos > -1;
    }

    @Override
    public void put(long pos, int val) {
        ensureCapacity(pos);
        mem[(int) pos] = val;
        if (lastPos < pos) {
            lastPos = pos;
        }
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
    public void release() {
        mem = null;
    }

    @Override
    public final IntMemIo clone() {
        IntMemIo cloned;
        try {
            cloned = (IntMemIo) super.clone();
            cloned.mem = Arrays.copyOf(mem, mem.length);
            return cloned;
        } catch (CloneNotSupportedException e) {
            return Crasher.crash(e);
        }
    }
}