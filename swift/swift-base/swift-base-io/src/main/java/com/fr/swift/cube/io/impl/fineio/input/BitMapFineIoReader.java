package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.util.Crasher;
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

    private static ImmutableBitMap getByHead(byte[] bytes) {
        byte head = bytes[0];
        // mutable，immutable底层都是同一结构，暂时先统一生成mutable
        if (head == BitMapType.ROARING_IMMUTABLE.getHead() || head == BitMapType.ROARING_MUTABLE.getHead()) {
            return RoaringMutableBitMap.ofBytes(bytes, 1, bytes.length - 1);
        }
        if (head == BitMapType.ALL_SHOW.getHead()) {
            return AllShowBitMap.ofBytes(bytes, 1);
        }
        if (head == BitMapType.RANGE.getHead() || head == BitMapType.ID.getHead()) {
            return RangeBitmap.ofBytes(bytes, 1);
        }
        return Crasher.crash("not a valid head or this bitmap doesn't support, head: " + head);
    }

    @Override
    public ImmutableBitMap get(long pos) {
        byte[] bytes = bar.get(pos);
        return getByHead(bytes);
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