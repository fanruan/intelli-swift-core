package com.fr.swift.cloud.cube.io.impl;

import com.fr.swift.cloud.cube.io.input.LongReader;
import com.fr.swift.cloud.cube.io.output.ByteWriter;
import com.fr.swift.cloud.cube.io.output.IntWriter;
import com.fr.swift.cloud.cube.io.output.LongWriter;
import com.fr.swift.cloud.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/3/27
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({IoUtil.class})
public class BaseByteArrayWriterTest {

    @Mock
    private ByteWriter dataWriter;
    @Mock
    private LongWriter posWriter;
    @Mock
    private IntWriter lenWriter;
    @Mock
    private LongWriter lastPosWriter;
    @Mock
    private LongReader lastPosReader;

    @Test
    public void put() {
        BaseByteArrayWriter byteArrayWriter = new BaseByteArrayWriter(dataWriter, posWriter, lenWriter, true, lastPosWriter, lastPosReader);
        byteArrayWriter.put(0, new byte[]{1, 2, 3});

        verifyZeroInteractions(lastPosWriter, lastPosReader);

        verify(posWriter).put(0, 0);
        verify(lenWriter).put(0, 3);
        verify(dataWriter).put(0, (byte) 1);
        verify(dataWriter).put(1, (byte) 2);
        verify(dataWriter).put(2, (byte) 3);

        assertEquals(3L, (long) Whitebox.getInternalState(byteArrayWriter, "curPos"));

        byteArrayWriter.put(1, null);

        verify(posWriter).put(1, 3);
        verify(lenWriter).put(1, 0);
        verifyNoMoreInteractions(dataWriter);

        assertEquals(3L, (long) Whitebox.getInternalState(byteArrayWriter, "curPos"));

        when(lastPosReader.isReadable()).thenReturn(true);
        when(lastPosReader.get(0)).thenReturn(3L);

        byteArrayWriter = new BaseByteArrayWriter(dataWriter, posWriter, lenWriter, false, lastPosWriter, lastPosReader);
        assertEquals(3L, (long) Whitebox.getInternalState(byteArrayWriter, "curPos"));
    }

    @Test
    public void putStream() throws IOException {
        BaseByteArrayWriter byteArrayWriter = new BaseByteArrayWriter(dataWriter, posWriter, lenWriter, true, lastPosWriter, lastPosReader);
        OutputStream output = byteArrayWriter.putStream(0);
        output.write(new byte[]{1, 2, 3});
        output.close();

        verifyZeroInteractions(lastPosWriter, lastPosReader);

        verify(posWriter).put(0, 0);
        verify(lenWriter).put(0, 3);
        verify(dataWriter).put(0, (byte) 1);
        verify(dataWriter).put(1, (byte) 2);
        verify(dataWriter).put(2, (byte) 3);

        assertEquals(3L, (long) Whitebox.getInternalState(byteArrayWriter, "curPos"));

        output = byteArrayWriter.putStream(1);
        output.close();

        verify(posWriter).put(1, 3);
        verify(lenWriter).put(1, 0);
        verifyNoMoreInteractions(dataWriter);

        assertEquals(3L, (long) Whitebox.getInternalState(byteArrayWriter, "curPos"));

        when(lastPosReader.isReadable()).thenReturn(true);
        when(lastPosReader.get(0)).thenReturn(3L);

        byteArrayWriter = new BaseByteArrayWriter(dataWriter, posWriter, lenWriter, false, lastPosWriter, lastPosReader);
        assertEquals(3L, (long) Whitebox.getInternalState(byteArrayWriter, "curPos"));
    }

    @Test
    public void resetContentPosition() {
        BaseByteArrayWriter byteArrayWriter = new BaseByteArrayWriter(dataWriter, posWriter, lenWriter, true, lastPosWriter, lastPosReader);
        Whitebox.setInternalState(byteArrayWriter, "curPos", 1L);

        byteArrayWriter.resetContentPosition();
        assertEquals(0L, (long) Whitebox.getInternalState(byteArrayWriter, "curPos"));
    }

    @Test
    public void release() {
        mockStatic(IoUtil.class);

        new BaseByteArrayWriter(dataWriter, posWriter, lenWriter, true, lastPosWriter, lastPosReader).release();

        verifyZeroInteractions(lastPosWriter);

        verifyStatic(IoUtil.class);
        IoUtil.release(lastPosWriter, dataWriter, posWriter, lenWriter);

        new BaseByteArrayWriter(dataWriter, posWriter, lenWriter, false, lastPosWriter, lastPosReader).release();
        verify(lastPosWriter).put(0, 0);

        verifyStatic(IoUtil.class, times(2));
        IoUtil.release(lastPosWriter, dataWriter, posWriter, lenWriter);

        doThrow(new RuntimeException()).when(lastPosWriter).put(0, 0);

        try {
            new BaseByteArrayWriter(dataWriter, posWriter, lenWriter, false, lastPosWriter, lastPosReader).release();
        } catch (Exception ignore) {
        }
        verify(lastPosWriter, times(2)).put(0, 0);

        verifyStatic(IoUtil.class, times(3));
        IoUtil.release(lastPosWriter, dataWriter, posWriter, lenWriter);
    }
}