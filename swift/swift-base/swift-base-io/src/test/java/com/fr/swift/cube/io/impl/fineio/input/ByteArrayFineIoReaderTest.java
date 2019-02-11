package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.ByteReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * @author anchore
 * @date 2019/1/4
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ByteFineIoReader.class, IntFineIoReader.class, LongFineIoReader.class})
public class ByteArrayFineIoReaderTest {

    private final URI location = URI.create("/cubes/table/seg0/column/detail");
    private final ByteReader byteReader = mock(ByteReader.class);
    private final IntReader intReader = mock(IntReader.class);
    private final LongReader longReader = mock(LongReader.class);

    @Before
    public void setUp() throws Exception {
        mockStatic(ByteFineIoReader.class);
        when(ByteFineIoReader.build(ArgumentMatchers.<URI>any())).thenReturn(byteReader);

        mockStatic(IntFineIoReader.class);
        when(IntFineIoReader.build(ArgumentMatchers.<URI>any())).thenReturn(intReader);

        mockStatic(LongFineIoReader.class);
        when(LongFineIoReader.build(ArgumentMatchers.<URI>any())).thenReturn(longReader);

        when(byteReader.isReadable()).thenReturn(true);
        when(intReader.isReadable()).thenReturn(true);
        when(longReader.isReadable()).thenReturn(true);

        when(byteReader.get(1)).thenReturn((byte) 1);
        when(intReader.get(0)).thenReturn(1);
        when(longReader.get(0)).thenReturn(1L);
    }

    @Test
    public void build() {
        ByteArrayFineIoReader.build(location);
    }

    @Test
    public void isReadable() {
        Assert.assertTrue(ByteArrayFineIoReader.build(location).isReadable());
    }

    @Test
    public void get() {
        Assert.assertArrayEquals(new byte[]{1}, ByteArrayFineIoReader.build(location).get(0));
    }

    @Test
    public void release() {
        ByteArrayFineIoReader.build(location).release();

        verify(byteReader).release();
        verify(intReader).release();
        verify(longReader).release();
    }
}