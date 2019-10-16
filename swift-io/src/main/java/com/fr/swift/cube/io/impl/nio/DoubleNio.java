package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.DoubleIo;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class DoubleNio extends BaseAtomNio implements DoubleIo {
    public DoubleNio(NioConf conf) {
        super(conf);
    }

    @Override
    int getStep() {
        return 3;
    }

    @Override
    public double get(long pos) {
        initBuf(nthBuf(pos));
        return buf.getDouble(bufOffset(pos));
    }

    @Override
    public void put(long pos, double val) {
        initBuf(nthBuf(pos));
        int offset = bufOffset(pos);
        buf.putDouble(offset, val);

        setBufPos(offset);
    }
}