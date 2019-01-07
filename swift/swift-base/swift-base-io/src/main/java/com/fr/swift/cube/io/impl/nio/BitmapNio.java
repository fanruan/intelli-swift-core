package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.cube.io.ObjectIo;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2018/7/22
 */
public class BitmapNio extends BaseNio implements BitMapReader, BitMapWriter, ObjectIo<ImmutableBitMap> {
    private ByteArrayNio obj;

    BitmapNio(NioConf conf) {
        super(conf);
        obj = new ByteArrayNio(conf);
    }


    @Override
    public void flush() {
    }

    @Override
    public ImmutableBitMap get(long pos) {
        byte[] bytes = obj.get(pos);
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
    public boolean isReadable() {
        return obj != null && obj.isReadable();
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
        obj.put(pos, combine);
    }

    @Override
    public void release() {
        IoUtil.release(obj);
        obj = null;
    }

    @Override
    public void resetContentPosition() {
        obj.resetContentPosition();
    }
}