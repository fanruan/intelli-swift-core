package com.fr.swift.cloud.cube.io.impl.nio;

import com.fr.swift.cloud.cube.io.ByteIo;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class ByteNio extends BaseAtomNio implements ByteIo {
    public ByteNio(NioConf conf) {
        super(conf);
    }

    @Override
    public byte get(long pos) {
        initBuf(nthBuf(pos));
        return buf.get(bufOffset(pos));
    }

    @Override
    public void put(long pos, byte val) {
        initBuf(nthBuf(pos));
        int offset = bufOffset(pos);
        buf.put(offset, val);

        setBufPos(offset);
    }

    @Override
    int getStep() {
        return 0;
    }
}