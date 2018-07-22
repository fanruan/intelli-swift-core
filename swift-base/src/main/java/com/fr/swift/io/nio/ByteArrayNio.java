package com.fr.swift.io.nio;

import com.fr.swift.io.ByteIo;
import com.fr.swift.io.IntIo;
import com.fr.swift.io.LongIo;
import com.fr.swift.io.ObjectIo;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class ByteArrayNio extends BaseNio implements ObjectIo<byte[]> {
    private LongIo position;
    private IntIo length;
    private ByteIo data;

    private long currentPos;

    public ByteArrayNio(NioConf conf) {
        super(conf);
        init();
    }


    private void init() {
        position = new LongNio(conf.ofAnotherPath(String.format("%s/%s", conf.getPath(), "pos")));
        length = new IntNio(conf.ofAnotherPath(String.format("%s/%s", conf.getPath(), "len")));
        data = new ByteNio(conf.ofAnotherPath(String.format("%s/%s", conf.getPath(), "data")));
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