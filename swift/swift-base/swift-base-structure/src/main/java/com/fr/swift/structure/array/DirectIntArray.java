package com.fr.swift.structure.array;

import com.fineio.memory.MemoryUtils;

/**
 * @author daniel
 * @date 2017/5/18
 */
public class DirectIntArray extends BaseIntArray {
    private long address;
    private int maxLen;
    private volatile boolean isClear = false;

    protected DirectIntArray(int len) {
        setValue(len);
        MemoryUtils.fill0(address, len << 2);
    }

    protected DirectIntArray(int len, int defaultValue) {
        setValue(len);
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(address, i, defaultValue);
        }
    }

    protected DirectIntArray(DirectIntArray old, int newLen) {
        if (old.isClear) {
            throw new RuntimeException("ERROR CLEARED");
        }
        maxLen = newLen;
        old.isClear = true;
        try {
            address = MemoryUtils.reallocate(old.address, newLen << 2);
        } catch (Exception e) {
            release();
            throw new RuntimeException("not enough memory");
        }
    }

    @Override
    public int size() {
        return maxLen;
    }

    private void setValue(int len) {
        if (len < 0) {
            throw new ArrayIndexOutOfBoundsException(len);
        }
        this.maxLen = len;
        try {
            address = MemoryUtils.allocate(len << 2);
        } catch (Exception e) {
            release();
            throw new RuntimeException("not enough memory");
        }
    }

    @Override
    public void put(int index, int value) {
        MemoryUtils.put(address, checkIndex(index), value);
    }

    private long checkIndex(long index) {
        if (isClear) {
            throw new RuntimeException("ERROR CLEARED");
        }
        if (index < 0 || index >= maxLen) {
            throw new ArrayIndexOutOfBoundsException((int) index);
        }
        return index;
    }

    @Override
    public int get(int index) {
        return MemoryUtils.getInt(address, checkIndex(index));
    }

    @Override
    public void release() {
        if (!isClear) {
            synchronized (this) {
                if (!isClear) {
                    MemoryUtils.free(address);
                    isClear = true;
                }
            }
        }
    }

}
