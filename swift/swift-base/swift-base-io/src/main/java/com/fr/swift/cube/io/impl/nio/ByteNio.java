package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.ByteIo;

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
        initBuf(getPage(pos));
        return buf.get(getOffset(pos));
    }

    @Override
    public void put(long pos, byte val) {
        initBuf(getPage(pos));
        int offset = getOffset(pos);
        buf.put(offset, val);

        setBufPosition(offset);
    }

    @Override
    int getStep() {
        return 0;
    }
}