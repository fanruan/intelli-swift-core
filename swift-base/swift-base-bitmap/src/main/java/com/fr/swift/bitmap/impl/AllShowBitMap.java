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
public final class AllShowBitMap extends RangeBitmap {
    private AllShowBitMap(int rowCount) {
        super(0, rowCount);
    }

    public static ImmutableBitMap of(int rowCount) {
        return new AllShowBitMap(rowCount);
    }

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(5)
                // 兼容fineio Bits的小端法
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(getType().getHead())
                .putInt(end).array();
    }

    @Override
    public void writeBytes(OutputStream output) {
        WritableByteChannel channel = Channels.newChannel(output);
        try {
            ByteBuffer buf = ByteBuffer.allocate(5)
                    // 兼容fineio Bits的小端法
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .put(getType().getHead())
                    .putInt(end);
            buf.flip();
            channel.write(buf);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            IoUtil.close(channel);
        }
    }

    @Override
    public ImmutableBitMap getAnd(ImmutableBitMap index) {
        return index;
    }

    @Override
    public ImmutableBitMap getOr(ImmutableBitMap index) {
        return this;
    }

    @Override
    public ImmutableBitMap getAndNot(ImmutableBitMap index) {
        return index.getNot(end);
    }

    @Override
    public ImmutableBitMap clone() {
        return of(end);
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ALL_SHOW;
    }
}