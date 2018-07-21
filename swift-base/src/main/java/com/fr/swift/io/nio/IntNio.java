package com.fr.swift.io.nio;

import com.fr.swift.io.IntIo;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class IntNio extends BaseAtomNio implements IntIo {
    public IntNio(String basePath) {
        super(basePath);
    }

    public IntNio(String basePath, int pageSize) {
        super(basePath, pageSize);
    }

    public static IntIo of(String basePath, int pageSize) {
        return new IntNio(basePath, pageSize);
    }

    public static IntIo of(String basePath) {
        return new IntNio(basePath);
    }

    @Override
    int getStep() {
        return 3;
    }

    @Override
    public int get(long pos) {
        initBuf(getPage(pos));
        return buf.getInt(getOffset(pos));
    }

    @Override
    public void put(long pos, int val) {
        initBuf(getPage(pos));
        int offset = getOffset(pos);
        buf.putInt(offset, val);

        setBufPosition(offset);
    }
}