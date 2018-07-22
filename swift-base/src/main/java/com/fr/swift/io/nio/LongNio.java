package com.fr.swift.io.nio;

import com.fr.swift.io.LongIo;

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
        initBuf(getPage(pos));
        return buf.getLong(getOffset(pos));
    }

    @Override
    public void put(long pos, long val) {
        initBuf(getPage(pos));
        int offset = getOffset(pos);
        buf.putLong(offset, val);

        setBufPosition(offset);
    }
}