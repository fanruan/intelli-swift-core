package com.fr.swift.cloud.cube.io.impl;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.cube.io.output.ByteArrayWriter;
import com.fr.swift.cloud.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.OutputStream;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/4/8
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({IoUtil.class})
public class BaseBitmapWriterTest {
    @Mock
    ByteArrayWriter byteArrayWriter;

    @Test
    public void resetContentPosition() {
        new BaseBitmapWriter(byteArrayWriter).resetContentPosition();

        verify(byteArrayWriter).resetContentPosition();
    }

    @Test
    public void put() {
        ImmutableBitMap bitmap = mock(ImmutableBitMap.class);
        OutputStream output = mock(OutputStream.class);
        when(byteArrayWriter.putStream(1)).thenReturn(output);

        new BaseBitmapWriter(byteArrayWriter).put(1, bitmap);

        verify(bitmap).writeBytes(output);
    }

    @Test
    public void release() {
        mockStatic(IoUtil.class);

        new BaseBitmapWriter(byteArrayWriter).release();

        verifyStatic(IoUtil.class);
        IoUtil.release(byteArrayWriter);
    }
}