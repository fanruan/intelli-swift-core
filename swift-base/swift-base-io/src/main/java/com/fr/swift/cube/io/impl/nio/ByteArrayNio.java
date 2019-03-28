package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.LongIo;
import com.fr.swift.cube.io.ObjectIo;
import com.fr.swift.cube.io.impl.BaseByteArrayReader;
import com.fr.swift.cube.io.impl.BaseByteArrayWriter;
import com.fr.swift.cube.io.impl.nio.NioConf.IoType;
import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2018/7/20
 */
public class ByteArrayNio extends BaseNio implements ObjectIo<byte[]>, ByteArrayWriter {

    private ByteArrayWriter byteArrayWriter;

    private ByteArrayReader byteArrayReader;

    public ByteArrayNio(NioConf conf) {
        super(conf);
        init();
    }

    private void init() {
        ByteNio data = new ByteNio(conf.ofAnotherPath(String.format("%s/%s", conf.getPath(), "data")));
        LongNio position = new LongNio(conf.ofAnotherPath(String.format("%s/%s", conf.getPath(), "pos")));
        IntNio length = new IntNio(conf.ofAnotherPath(String.format("%s/%s", conf.getPath(), "len")));
        LongIo lastPosition = conf.isOverwrite() ? null :
                new LongNio(new NioConf(String.format("%s/%s", conf.getPath(), "last_pos"), IoType.OVERWRITE, conf.getBufSize(), conf.getFileSize(), conf.isMapped()));

        byteArrayWriter = new BaseByteArrayWriter(data, position, length, conf.isOverwrite(), lastPosition, lastPosition);
        byteArrayReader = new BaseByteArrayReader(data, position, length);
    }

    @Override
    public void put(long pos, byte[] val) {
        byteArrayWriter.put(pos, val);
    }

    @Override
    public byte[] get(long pos) {
        return byteArrayReader.get(pos);
    }

    @Override
    public void resetContentPosition() {
        byteArrayWriter.resetContentPosition();
    }

    @Override
    public boolean isReadable() {
        return byteArrayReader.isReadable();
    }

    @Override
    public void release() {
        IoUtil.release(byteArrayWriter, byteArrayReader);
    }
}