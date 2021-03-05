package com.fr.swift.cloud.cube.io.impl;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.cube.io.input.BitMapReader;
import com.fr.swift.cloud.cube.io.input.ByteArrayReader;
import com.fr.swift.cloud.util.IoUtil;

/**
 * @author anchore
 * @date 2019/4/4
 */
public class BaseBitmapReader implements BitMapReader {
    private ByteArrayReader byteArrayReader;

    public BaseBitmapReader(ByteArrayReader byteArrayReader) {
        this.byteArrayReader = byteArrayReader;
    }

    @Override
    public boolean isReadable() {
        return byteArrayReader != null && byteArrayReader.isReadable();
    }

    @Override
    public ImmutableBitMap get(long pos) {
        return BitMaps.ofStream(byteArrayReader.getStream(pos));
    }

    @Override
    public void release() {
        IoUtil.release(byteArrayReader);
    }
}