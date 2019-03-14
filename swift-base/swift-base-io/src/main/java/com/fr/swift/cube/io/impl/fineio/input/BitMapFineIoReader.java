package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.util.IoUtil;

import java.net.URI;

/**
 * @author anchore
 */
public class BitMapFineIoReader implements BitMapReader {
    private ByteArrayReader bar;

    private BitMapFineIoReader(ByteArrayReader bar) {
        this.bar = bar;
    }

    public static BitMapReader build(URI location) {
        // 底层为byte array reader
        return new BitMapFineIoReader(ByteArrayFineIoReader.build(location));
    }

    @Override
    public ImmutableBitMap get(long pos) {
        byte[] bytes = bar.get(pos);
        return BitMaps.of(bytes);
    }

    @Override
    public boolean isReadable() {
        return bar.isReadable();
    }

    @Override
    public void release() {
        IoUtil.release(bar);
    }
}