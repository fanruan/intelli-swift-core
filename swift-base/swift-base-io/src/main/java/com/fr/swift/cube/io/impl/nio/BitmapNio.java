package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.ObjectIo;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.output.BitMapWriter;
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
        return BitMaps.of(bytes);
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