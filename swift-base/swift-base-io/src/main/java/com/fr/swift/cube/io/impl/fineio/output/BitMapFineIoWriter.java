package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.ByteArrayWriter;

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
    public void flush() {
        baw.flush();
    }

    @Override
    public void release() {
        baw.release();
    }

    @Override
    public void put(long pos, ImmutableBitMap val) {
        byte[] combine = null;
        if (val != null) {
            byte head = val.getType().getHead();
            byte[] bytes = val.toBytes();
            combine = new byte[bytes.length + 1];
            combine[0] = head;
            System.arraycopy(bytes, 0, combine, 1, bytes.length);
        }
        baw.put(pos, combine);
    }

    @Override
    public void resetContentPosition() {
        baw.resetContentPosition();
    }
}