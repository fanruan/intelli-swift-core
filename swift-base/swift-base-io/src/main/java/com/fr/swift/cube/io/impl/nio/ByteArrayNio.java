package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.ByteIo;
import com.fr.swift.cube.io.IntIo;
import com.fr.swift.cube.io.LongIo;
import com.fr.swift.cube.io.ObjectIo;
import com.fr.swift.cube.io.impl.nio.NioConf.IoType;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class ByteArrayNio extends BaseNio implements ObjectIo<byte[]>, ByteArrayWriter {
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
        if (conf.isAppend()) {
            LongIo lastPosition = new LongNio(new NioConf(
                    String.format("%s/%s", conf.getPath(), "last_pos"), IoType.READ, conf.getBufSize(), conf.getFileSize(), conf.isMapped()));
            currentPos = lastPosition.isReadable() ? lastPosition.get(0) : 0;
            lastPosition.release();
        }
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
    public void put(long pos, byte[] val) {
        position.put(pos, currentPos);
        length.put(pos, val.length);
        for (byte b : val) {
            data.put(currentPos++, b);
        }
    }

    @Override
    public void resetContentPosition() {
        currentPos = 0;
    }

    @Override
    public void flush() {
    }

    @Override
    public boolean isReadable() {
        return position != null && position.isReadable() &&
                length != null && length.isReadable() &&
                data != null && data.isReadable();
    }

    @Override
    public void release() {
        if (conf.isAppend()) {
            LongIo lastPosition = new LongNio(new NioConf(
                    String.format("%s/%s", conf.getPath(), "last_pos"), IoType.OVERWRITE, conf.getBufSize(), conf.getFileSize(), conf.isMapped()));
            lastPosition.put(0, currentPos);
            lastPosition.release();
        }
        IoUtil.release(data, position, length);
        data = null;
        position = null;
        length = null;
    }
}