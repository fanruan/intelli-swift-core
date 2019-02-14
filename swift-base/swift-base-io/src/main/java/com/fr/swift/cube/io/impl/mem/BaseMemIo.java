package com.fr.swift.cube.io.impl.mem;

/**
 * 基于内存的读写
 * ps：读写不上锁
 *
 * @author anchore
 * @date 2017/11/23
 */
abstract class BaseMemIo implements MemIo {

    static final int DEFAULT_CAPACITY = 16;

    private static final int EXPAND_SCALE = 1;

    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    long lastPos = -1;

    static int expand(int oldLen, long pos) {
        int newLen = oldLen;
        for (int tmp; newLen <= pos; newLen = tmp) {
            if ((tmp = newLen << EXPAND_SCALE) < newLen) {
                newLen = MAX_ARRAY_SIZE;
                break;
            }
        }
        return newLen;
    }

    /**
     * 保证能放得下pos位置的数据
     * 暂只处理小于Integer.MAX_VALUE的情况
     *
     * @param pos 位置
     */
    abstract void ensureCapacity(long pos);

    /**
     * 防止扩容后，多余的空间被访问到
     *
     * @param pos 位置
     * @throws ArrayIndexOutOfBoundsException 多余的空间被访问到时抛出
     */
    void checkPosition(long pos) throws ArrayIndexOutOfBoundsException {
        if (pos > lastPos) {
            throw new ArrayIndexOutOfBoundsException((int) pos);
        }
    }

    /**
     * no buffer to flush
     */
    @Override
    public final void flush() {
    }
}
