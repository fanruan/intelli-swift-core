package com.fr.swift.io.nio;

import com.fr.swift.io.ByteIo;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class ByteNio extends BaseAtomNio implements ByteIo {
    public ByteNio(String basePath) {
        super(basePath);
    }

    public ByteNio(String basePath, int pageSize) {
        super(basePath, pageSize);
    }


    public static ByteIo of(String basePath, int pageSize) {
        return new ByteNio(basePath, pageSize);
    }

    public static ByteIo of(String basePath) {
        return new ByteNio(basePath);
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