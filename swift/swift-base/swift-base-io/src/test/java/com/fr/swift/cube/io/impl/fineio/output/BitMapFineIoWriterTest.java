package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.bitmap.impl.EmptyBitmap;
import com.fr.swift.cube.io.output.ByteArrayWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.powermock.api.mockito.PowerMockito.doNothing;
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

    @Before
    public void setUp() throws Exception {
        mockStatic(ByteArrayFineIoWriter.class);
        ByteArrayWriter byteArrayWriter = mock(ByteArrayWriter.class);
        when(ByteArrayFineIoWriter.build(Matchers.<URI>any(), anyBoolean())).thenReturn(byteArrayWriter);

        doNothing().when(byteArrayWriter).put(anyLong(), Matchers.<byte[]>any());
    }

    @Test
    public void build() {
        BitMapFineIoWriter.build(location, true);
    }

    @Test
    public void get() {
        BitMapFineIoWriter.build(location, true).put(0, new EmptyBitmap());
        BitMapFineIoWriter.build(location, true).put(1, null);
    }

    @Test
    public void release() {
        BitMapFineIoWriter.build(location, true).release();
    }
}