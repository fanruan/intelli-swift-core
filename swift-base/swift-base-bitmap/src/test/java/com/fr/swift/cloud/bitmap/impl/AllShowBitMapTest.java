package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.BitMapType;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.MutableBitMap;
import com.fr.swift.cloud.util.IoUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * @author anchore
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IoUtil.class, AllShowBitMap.class, Channels.class})
public class AllShowBitMapTest {
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
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap and = im.getAnd(m);
        for (int i = 0; i < a.length; i++) {
            if ((a[i] == 1) != and.contains(i)) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testGetOr() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap or = im.getOr(m);
        for (int i = 0; i < rowCount; i++) {
            if (!or.contains(i)) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testGetAndNot() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap andNot = im.getAndNot(m);
        for (int i = 0; i < a.length; i++) {
            if ((a[i] == 1) == andNot.contains(i)) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testGetNot() {
        int rowCount = r.nextInt(BOUND);
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap not = im.getNot(rowCount);
        if (!not.isEmpty()) {
            Assert.fail();
        }
    }

    private int rand(int from, int to) {
        return r.nextInt(to - from) + from;
    }

    @Test
    public void of() {
        ImmutableBitMap bitmap = AllShowBitMap.of(100);
        Assert.assertEquals(100, bitmap.getCardinality());
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(bitmap.contains(i));
        }
    }

    @Test
    public void toBytes() {
        assertArrayEquals(AllShowBitMap.of(1).toBytes(),
                ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
                        .put(BitMapType.ALL_SHOW.getHead()).putInt(1).array());
    }

    @Test
    public void writeBytes() throws Exception {
        OutputStream output = mock(OutputStream.class);

        mockStatic(IoUtil.class);

        AllShowBitMap.of(1).writeBytes(output);

        ByteBuffer buf = ByteBuffer.allocate(5)
                // 兼容fineio Bits的小端法
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(BitMapType.ALL_SHOW.getHead())
                .putInt(1);

        verify(output).write(buf.array());

        doThrow(new IOException()).when(output).write(any(byte[].class));

        AllShowBitMap.of(1).writeBytes(output);

        verifyStatic(IoUtil.class, times(2));
        IoUtil.close(output);
    }

    @Test
    public void getNot() {
        BitmapAssert.equals(new RangeBitmap(5, 10), AllShowBitMap.of(10).getNot(5));
        BitmapAssert.equals(new RangeBitmap(10, 15), AllShowBitMap.of(10).getNot(15));
        BitmapAssert.equals(new EmptyBitmap(), AllShowBitMap.of(10).getNot(10));
    }
}
