package com.fr.swift.structure.array;


import com.fineio.memory.MemoryUtils;

/**
 * @author yee
 * @date 2018/4/2
 */
public class DirectLongArray implements LongArray {
    private long address;
    private int maxLen;
    private volatile boolean isClear = false;

    protected DirectLongArray(int len) {
        setValue(len);
        MemoryUtils.fill0(address, len << 3);
    }

    protected DirectLongArray(int len, long defaultValue) {
        setValue(len);
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(address, i, defaultValue);
        }
    }

    protected DirectLongArray(DirectLongArray old, int newLen) {
        if (old.isClear) {
            throw new RuntimeException("ERROR CLEARED");
        }
        maxLen = newLen;
        old.isClear = true;
        try {
            address = MemoryUtils.reallocate(old.address, newLen << 3);
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
            address = MemoryUtils.allocate(len << 3);
        } catch (Exception e) {
            release();
            throw new RuntimeException("not enough memory");
        }
    }

    @Override
    public void put(int index, long value) {
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
    public long get(int index) {
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

    @Override
    public LongArray clone() {
        LongArray array = new DirectLongArray(maxLen);
        for (int i = 0; i < maxLen; i++) {
            array.put(i, get(i));
        }
        return array;
    }

}
