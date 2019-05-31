package com.fr.swift.cube.io.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2019/4/4
 */
public class BaseBitmapWriter implements BitMapWriter {
    private ByteArrayWriter byteArrayWriter;

    public BaseBitmapWriter(ByteArrayWriter byteArrayWriter) {
        this.byteArrayWriter = byteArrayWriter;
    }

    @Override
    public void resetContentPosition() {
        byteArrayWriter.resetContentPosition();
    }

    @Override
    public void put(long pos, ImmutableBitMap val) {
        val.writeBytes(byteArrayWriter.putStream(pos));
    }

    @Override
    public void release() {
        IoUtil.release(byteArrayWriter);
    }
}