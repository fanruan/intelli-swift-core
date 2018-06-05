package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 */
public class BitMapFineIoReader extends BaseFineIoReader implements BitMapReader {
    private ByteArrayReader bar;

    private BitMapFineIoReader(ByteArrayReader bar) {
        this.bar = bar;
    }

    public static BitMapReader build(IResourceLocation location) {
        // 底层为byte array reader
        return new BitMapFineIoReader(ByteArrayFineIoReader.build(location));
    }

    @Override
    public ImmutableBitMap get(long pos) {
        byte[] bytes = bar.get(pos);
        return getByHead(bytes);
    }

    private static ImmutableBitMap getByHead(byte[] bytes) {
        byte head = bytes[0];
        // mutable，immutable底层都是同一结构，暂时先统一生成mutable
        if (head == BitMapType.ROARING_IMMUTABLE.head || head == BitMapType.ROARING_MUTABLE.head) {
            return RoaringMutableBitMap.fromBytes(bytes, 1, bytes.length - 1);
        }
        if (head == BitMapType.RANGE.head || head == BitMapType.ALL_SHOW.head || head == BitMapType.ID.head) {
            return RangeBitmap.fromBytes(bytes, 1);
        }
        return Crasher.crash("not a valid head or this bitmap doesn't support, head: " + head);
    }

    @Override
    public long getLastPosition(long pos) {
        return bar.getLastPosition(pos);
    }

    @Override
    public boolean isReadable() {
        return bar.isReadable();
    }
}