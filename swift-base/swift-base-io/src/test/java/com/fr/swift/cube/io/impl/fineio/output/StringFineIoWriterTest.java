package com.fr.swift.cube.io.impl.fineio.output;

import com.fr.swift.cube.io.output.ByteArrayWriter;
import com.fr.swift.cube.io.output.StringWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.mockito.ArgumentMatchers.anyBoolean;
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
public class StringFineIoWriterTest {

    private ByteArrayWriter byteArrayWriter = mock(ByteArrayWriter.class);

    @Before
    public void setUp() throws Exception {
        mockStatic(ByteArrayFineIoWriter.class);
        when(ByteArrayFineIoWriter.build(ArgumentMatchers.<URI>any(), anyBoolean())).thenReturn(byteArrayWriter);
    }

    @Test
    public void build() {
        StringFineIoWriter.build(URI.create(""), true);
    }

    @Test
    public void put() {
        StringWriter stringWriter = StringFineIoWriter.build(URI.create(""), true);
        stringWriter.put(0, "\1\2\3");
        stringWriter.put(1, null);

        verify(byteArrayWriter).put(0L, "\1\2\3".getBytes(StringWriter.CHARSET));
        verify(byteArrayWriter).put(1L, null);
    }

    @Test
    public void release() {
        StringFineIoWriter.build(URI.create(""), true).release();
        verify(byteArrayWriter).release();
    }
}