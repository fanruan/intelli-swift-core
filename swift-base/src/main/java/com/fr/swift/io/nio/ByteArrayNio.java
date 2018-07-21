package com.fr.swift.io.nio;

import com.fr.swift.io.ByteIo;
import com.fr.swift.io.IntIo;
import com.fr.swift.io.LongIo;
import com.fr.swift.io.SameObjectIo;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class ByteArrayNio extends BaseNio implements SameObjectIo<byte[]> {
    private LongIo position;
    private IntIo length;
    private ByteIo data;

    private long currentPos;

    public ByteArrayNio(String basePath, int pageSize) {
        super(basePath);
        init(pageSize);
    }

    public static SameObjectIo<byte[]> of(String basePath, int pageSize) {
        return new ByteArrayNio(basePath, pageSize);
    }

    private void init(int pageSize) {
        position = LongNio.of(String.format("%s/%s", basePath, "pos"), pageSize);
        length = IntNio.of(String.format("%s/%s", basePath, "len"), pageSize);
        data = ByteNio.of(String.format("%s/%s", basePath, "data"), pageSize);
    }

    @Override
    public byte[] get(long pos) {
        long start = position.get(pos);
        int size = length.get(pos);
        byte[] bytes = new byte[size];
        for (long i = 0; i < size; i++) {
            bytes[(int) i] = data.get(start + i);
        }
        return bytes;
    }

    @Override
    public long getLastPosition(long pos) {
        return 0;
    }

    @Override
    public void put(long pos, byte[] val) {
        position.put(pos, currentPos);
        length.put(pos, val.length);
        for (byte b : val) {
            data.put(currentPos++, b);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public void release() {
        IoUtil.release(data, position, length);
        data = null;
        position = null;
        length = null;
    }
}