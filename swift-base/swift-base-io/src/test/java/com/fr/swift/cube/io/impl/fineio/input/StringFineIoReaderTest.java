package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.ByteArrayReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/4
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ByteArrayFineIoReader.class})
public class StringFineIoReaderTest {

    private final ByteArrayReader byteArrayReader = mock(ByteArrayReader.class);

    @Before
    public void setUp() throws Exception {
        mockStatic(ByteArrayFineIoReader.class);
        when(ByteArrayFineIoReader.build(ArgumentMatchers.<URI>any())).thenReturn(byteArrayReader);

        when(byteArrayReader.get(0)).thenReturn(new byte[]{1, 2, 3});

        when(byteArrayReader.isReadable()).thenReturn(true);
    }

    @Test
    public void build() {
        StringFineIoReader.build(URI.create(""));
    }

    @Test
    public void get() {
        assertEquals("\1\2\3", StringFineIoReader.build(URI.create("")).get(0));
    }

    @Test
    public void isReadable() {
        assertTrue(StringFineIoReader.build(URI.create("")).isReadable());
    }

    @Test
    public void release() {
        StringFineIoReader.build(URI.create("")).release();

        verify(byteArrayReader).release();
    }
}