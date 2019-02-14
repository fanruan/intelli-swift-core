package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.bitmap.impl.EmptyBitmap;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/4
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ByteArrayFineIoWriter.class})
public class BitMapFineIoWriterTest {

    private final URI location = URI.create("/cubes/table/seg0/column/detail");

    private final ByteArrayWriter byteArrayWriter = mock(ByteArrayWriter.class);

    @Before
    public void setUp() throws Exception {
        mockStatic(ByteArrayFineIoWriter.class);
        when(ByteArrayFineIoWriter.build(ArgumentMatchers.<URI>any(), anyBoolean())).thenReturn(byteArrayWriter);
    }

    @Test
    public void build() {
        BitMapFineIoWriter.build(location, true);
    }

    @Test
    public void put() {
        BitMapFineIoWriter.build(location, true).put(0, new EmptyBitmap());
        BitMapFineIoWriter.build(location, true).put(1, null);

        verify(byteArrayWriter).put(anyLong(), notNull(byte[].class));
        verify(byteArrayWriter).put(anyLong(), isNull(byte[].class));
    }

    @Test
    public void release() {
        BitMapFineIoWriter.build(location, true).release();

        verify(byteArrayWriter).release();
    }
}