package com.fr.swift.bitmap;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.bitmap.impl.EmptyBitmap;
import com.fr.swift.bitmap.impl.IdBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.structure.array.HeapIntArray;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.Assert;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author anchore
 */
public final class BitMaps {
    public static final ImmutableBitMap EMPTY_IMMUTABLE = new EmptyBitmap();

    public static MutableBitMap newRoaringMutable() {
        return RoaringMutableBitMap.of();
    }

    public static MutableBitMap newBitSetMutable() {
        return BitSetMutableBitMap.newInstance();
    }

    public static ImmutableBitMap newAllShowBitMap(int rowCount) {
        return AllShowBitMap.of(rowCount);
    }

    public static ImmutableBitMap newRangeBitmap(int start, int end) {
        return new RangeBitmap(start, end);
    }

    public static ImmutableBitMap newIdBitMap(int id) {
        return IdBitMap.of(id);
    }

    public static ImmutableBitMap newImmutableBitMap(IntList intList) {
        final MutableBitMap bitmap = RoaringMutableBitMap.of();
        new IntListRowTraversal(intList).traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                bitmap.add(row);
            }
        });
        return bitmap;
    }

    public static IntArray traversal2Array(RowTraversal traversal) {
        final IntArray array = new HeapIntArray(traversal.getCardinality());
        traversal.traversal(new TraversalAction() {
            private int index = 0;

            @Override
            public void actionPerformed(int row) {
                array.put(index++, row);
            }
        });
        return array;
    }

    public static ImmutableBitMap ofStream(InputStream input) {
        Assert.notNull(input);

        try {
            BitMapType type = BitMapType.ofHead((byte) input.read());
            switch (type) {
                case ROARING_IMMUTABLE:
                case ROARING_MUTABLE:
                    // mutable，immutable底层都是同一结构，暂时先统一生成mutable
                    return RoaringMutableBitMap.ofStream(input);
                case EMPTY:
                    return EMPTY_IMMUTABLE;
                default:
                    // DataInputStream只能大端，秒切NIO
                    ReadableByteChannel channel = Channels.newChannel(input);
                    return ofChannel(channel, type);
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return EMPTY_IMMUTABLE;
        } finally {
            IoUtil.close(input);
        }
    }

    private static ImmutableBitMap ofChannel(ReadableByteChannel channel, BitMapType type) throws IOException {
        // 兼容fineio Bits的小端法
        ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        try {
            channel.read(buf);
            buf.flip();
        } finally {
            IoUtil.close(channel);
        }
        switch (type) {
            case ALL_SHOW:
                return AllShowBitMap.of(buf.getInt());
            case RANGE:
                return RangeBitmap.of(buf.getInt(), buf.getInt());
            case ID:
                return IdBitMap.of(buf.getInt());
            default:
                return Crasher.crash(String.format("not a valid type or this bitmap doesn't support %s", type));
        }
    }
}
