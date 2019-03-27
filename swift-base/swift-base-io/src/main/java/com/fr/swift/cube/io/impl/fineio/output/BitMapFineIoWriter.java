package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.util.IoUtil;

import java.net.URI;

/**
 * @author anchore
 */
public class BitMapFineIoWriter implements BitMapWriter {

    private ByteArrayWriter baw;

    private BitMapFineIoWriter(ByteArrayWriter baw) {
        this.baw = baw;
    }

    public static BitMapWriter build(URI location, boolean isOverwrite) {
        return new BitMapFineIoWriter(ByteArrayFineIoWriter.build(location, isOverwrite));
    }

    @Override
    public void put(long pos, ImmutableBitMap val) {
        baw.put(pos, val == null ? null : val.toBytes());
    }

    @Override
    public void resetContentPosition() {
        baw.resetContentPosition();
    }

    @Override
    public void release() {
        IoUtil.release(baw);
    }
}