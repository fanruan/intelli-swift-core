package com.fr.swift.io.nio;

import com.fr.swift.io.LongIo;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class LongNio extends BaseAtomNio implements LongIo {
    public LongNio(String basePath) {
        super(basePath);
    }

    public LongNio(String basePath, int pageSize) {
        super(basePath, pageSize);
    }

    public static LongIo of(String basePath) {
        return new LongNio(basePath);
    }

    public static LongIo of(String basePath, int pageSize) {
        return new LongNio(basePath, pageSize);
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