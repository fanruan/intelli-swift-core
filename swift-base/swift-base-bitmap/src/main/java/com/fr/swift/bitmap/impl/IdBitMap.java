package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.IoUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * @author anchore
 */
public final class IdBitMap extends RangeBitmap {
    private IdBitMap(int id) {
        super(id, id + 1);
    }

    public static ImmutableBitMap of(int id) {
        return new IdBitMap(id);
    }

    @Override
    public ImmutableBitMap clone() {
        return of(start);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ID;
    }

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(5)
                // 兼容fineio Bits的小端法
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(getType().getHead())
                .putInt(start).array();
    }

    @Override
    public void writeBytes(OutputStream output) {
        WritableByteChannel channel = Channels.newChannel(output);
        try {
            ByteBuffer buf = ByteBuffer.allocate(5)
                    // 兼容fineio Bits的小端法
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .put(getType().getHead())
                    .putInt(start);
            buf.flip();
            channel.write(buf);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            IoUtil.close(channel);
        }
    }

    @Override
    public String toString() {
        return String.format("{%d}", start);
    }
}