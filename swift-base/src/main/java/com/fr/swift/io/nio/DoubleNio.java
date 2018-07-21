package com.fr.swift.io.nio;

import com.fr.swift.io.DoubleIo;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class DoubleNio extends BaseAtomNio implements DoubleIo {
    public DoubleNio(String basePath) {
        super(basePath);
    }

    public DoubleNio(String basePath, int pageSize) {
        super(basePath, pageSize);
    }

    public static DoubleIo of(String basePath) {
        return new DoubleNio(basePath);
    }

    public static DoubleIo of(String basePath, int pageSize) {
        return new DoubleNio(basePath, pageSize);
    }

    @Override
    int getStep() {
        return 3;
    }

    @Override
    public double get(long pos) {
        initBuf(getPage(pos));
        return buf.getDouble(getOffset(pos));
    }

    @Override
    public void put(long pos, double val) {
        initBuf(getPage(pos));
        int offset = getOffset(pos);
        buf.putDouble(offset, val);

        setBufPosition(offset);
    }
}