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