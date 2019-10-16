package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.LongIo;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class LongNio extends BaseAtomNio implements LongIo {
    public LongNio(NioConf conf) {
        super(conf);
    }

    @Override
    int getStep() {
        return 3;
    }

    @Override
    public long get(long pos) {
        initBuf(nthBuf(pos));
        return buf.getLong(bufOffset(pos));
    }

    @Override
    public void put(long pos, long val) {
        initBuf(nthBuf(pos));
        int offset = bufOffset(pos);
        buf.putLong(offset, val);

        setBufPos(offset);
    }
}