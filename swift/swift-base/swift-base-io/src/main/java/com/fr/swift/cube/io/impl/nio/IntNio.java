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