package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.BitMapType;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * @author anchore
 * @date 2019/3/12
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IoUtil.class, RangeBitmap.class})
public class RangeBitmapTest {

    @Test
    public void of() {
        ImmutableBitMap bitmap = RangeBitmap.of(100, 200);
        assertEquals(100, bitmap.getCardinality());
        for (int i = 100; i < 200; i++) {
            Assert.assertTrue(bitmap.contains(i));
        }
    }

    @Test
    public void toBytes() {
        assertArrayEquals(RangeBitmap.of(1, 3).toBytes(),
                ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN)
                        .put(BitMapType.RANGE.getHead()).putInt(1).putInt(3).array());
    }

    @Test
    public void writeBytes() throws Exception {
        OutputStream output = mock(OutputStream.class);

        mockStatic(IoUtil.class);

        RangeBitmap.of(1, 2).writeBytes(output);


        ByteBuffer buf = ByteBuffer.allocate(9)
                // 兼容fineio Bits的小端法
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(BitMapType.RANGE.getHead())
                .putInt(1).putInt(2);

        verify(output).write(buf.array());

        doThrow(new IOException()).when(output).write(any(byte[].class));

        RangeBitmap.of(1, 2).writeBytes(output);

        verifyStatic(IoUtil.class, times(2));
        IoUtil.close(output);
    }
}