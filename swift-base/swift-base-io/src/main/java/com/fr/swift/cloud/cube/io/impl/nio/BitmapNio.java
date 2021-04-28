package com.fr.swift.cloud.cube.io.impl.nio;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.cube.io.ObjectIo;
import com.fr.swift.cloud.cube.io.impl.BaseBitmapReader;
import com.fr.swift.cloud.cube.io.impl.BaseBitmapWriter;
import com.fr.swift.cloud.cube.io.input.BitMapReader;
import com.fr.swift.cloud.cube.io.output.BitMapWriter;
import com.fr.swift.cloud.util.IoUtil;

/**
 * @author anchore
 * @date 2018/7/22
 */
public class BitmapNio implements BitMapReader, BitMapWriter, ObjectIo<ImmutableBitMap> {
    private BitMapWriter bitmapWriter;
    private BitMapReader bitmapReader;

    public BitmapNio(NioConf conf) {
        ByteArrayNio byteArrayNio = new ByteArrayNio(conf);
        this.bitmapWriter = new BaseBitmapWriter(byteArrayNio);
        this.bitmapReader = new BaseBitmapReader(byteArrayNio);
    }

    @Override
    public void put(long pos, ImmutableBitMap val) {
        bitmapWriter.put(pos, val);
    }

    @Override
    public ImmutableBitMap get(long pos) {
        return bitmapReader.get(pos);
    }

    @Override
    public boolean isReadable() {
        return bitmapReader != null && bitmapReader.isReadable();
    }

    @Override
    public void resetContentPosition() {
        bitmapWriter.resetContentPosition();
    }

    @Override
    public void release() {
        IoUtil.release(bitmapWriter, bitmapReader);
    }
}