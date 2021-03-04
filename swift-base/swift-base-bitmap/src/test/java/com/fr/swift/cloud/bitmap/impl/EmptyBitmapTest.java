package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.BitMapType;
import com.fr.swift.cloud.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.OutputStream;

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
 * @date 2019/3/26
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IoUtil.class, EmptyBitmap.class})
public class EmptyBitmapTest {
    @Test
    public void toBytes() {
        assertArrayEquals(new byte[]{BitMapType.EMPTY.getHead()}, new EmptyBitmap().toBytes());
    }

    @Test
    public void writeBytes() throws Exception {
        OutputStream output = mock(OutputStream.class);

        mockStatic(IoUtil.class);

        new EmptyBitmap().writeBytes(output);

        verify(output).write(new byte[]{BitMapType.EMPTY.getHead()});

        doThrow(new IOException()).when(output).write(any(byte[].class));

        new EmptyBitmap().writeBytes(output);

        verifyStatic(IoUtil.class, times(2));
        IoUtil.close(output);
    }

    @Test
    public void getType() {
        assertEquals(BitMapType.EMPTY, new EmptyBitmap().getType());
    }
}