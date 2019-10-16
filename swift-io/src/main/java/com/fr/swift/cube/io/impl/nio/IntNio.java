package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.IntIo;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class IntNio extends BaseAtomNio implements IntIo {
    public IntNio(NioConf conf) {
        super(conf);
    }

    @Override
    int getStep() {
        return 2;
    }

    @Override
    public int get(long pos) {
        initBuf(nthBuf(pos));
        return buf.getInt(bufOffset(pos));
    }

    @Override
    public void put(long pos, int val) {
        initBuf(nthBuf(pos));
        int offset = bufOffset(pos);
        buf.putInt(offset, val);

        setBufPos(offset);
    }
}