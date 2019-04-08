package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IoUtil.class, IdBitMap.class})
public class IdBitMapTest {
    private final Random r = new Random();
    private static final int BOUND = 1000000;

    private MutableBitMap getMutableBitMap() {
        return RoaringMutableBitMap.of();
    }

    private int[] prepare(MutableBitMap m) {
        int[] a = new int[rand(2, BOUND)];

        for (int i = 0; i < a.length / 2; i++) {
            int ri = r.nextInt(a.length);
            a[ri] = 1;
            m.add(ri);
        }
        return a;
    }

    @Test
    public void testGetAnd() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int id = r.nextInt(BOUND);
        ImmutableBitMap im = IdBitMap.of(id);

        ImmutableBitMap and = im.getAnd(m);
        for (int i = 0; i < a.length; i++) {
            if (i != id && and.contains(i)) {
                fail();
            }
        }
        int tmp = id < a.length ? a[id] : 0;
        if ((tmp == 1) != and.contains(id)) {
            fail();
        }
    }

    @Test
    public void testGetOr() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int id = r.nextInt(BOUND);
        ImmutableBitMap im = IdBitMap.of(id);

        ImmutableBitMap or = im.getOr(m);
        for (int i = 0; i < a.length; i++) {
            if (i != id) {
                if ((a[i] == 1) != or.contains(i)) {
                    fail();
                }
            }
        }
        if (!or.contains(id)) {
            fail();
        }
    }

    @Test
    public void testGetAndNot() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int id = r.nextInt(BOUND);
        ImmutableBitMap im = IdBitMap.of(id);

        ImmutableBitMap andNot = im.getAndNot(m);
        for (int i = 0; i < a.length; i++) {
            if (i != id && andNot.contains(i)) {
                fail();
            }
        }

        if (m.contains(id) == andNot.contains(id)) {
            fail();
        }
    }

    @Test
    public void testGetNot() {
        int rowCount = r.nextInt(BOUND);
        int id = r.nextInt(rowCount);
        ImmutableBitMap im = IdBitMap.of(id);

        ImmutableBitMap not = im.getNot(rowCount);
        for (int i = 0; i < rowCount; i++) {
            if (i != id && !not.contains(i)) {
                fail();
            }
        }
        if (not.contains(id)) {
            fail();
        }
    }

    private int rand(int from, int to) {
        return r.nextInt(to - from) + from;
    }

    @Test
    public void toBytes() {
        assertArrayEquals(IdBitMap.of(1).toBytes(),
                ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
                        .put(BitMapType.ID.getHead()).putInt(1).array());
    }

    @Test
    public void writeBytes() throws Exception {
        OutputStream output = mock(OutputStream.class);
        WritableByteChannel channel = mock(WritableByteChannel.class);
        mockStatic(Channels.class);
        when(Channels.newChannel(output)).thenReturn(channel);

        mockStatic(IoUtil.class);

        IdBitMap.of(1).writeBytes(output);


        ByteBuffer buf = ByteBuffer.allocate(5)
                // 兼容fineio Bits的小端法
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(BitMapType.ID.getHead())
                .putInt(1);
        buf.flip();

        verify(channel).write(buf);

        doThrow(new IOException()).when(channel).write(any(ByteBuffer.class));

        IdBitMap.of(1).writeBytes(output);

        verifyStatic(IoUtil.class, times(2));
        IoUtil.close(channel);
    }
}
