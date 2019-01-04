package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.ByteReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.powermock.api.mockito.PowerMockito.doNothing;
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

    @Before
    public void setUp() throws Exception {
        mockStatic(ByteFineIoReader.class);
        ByteReader byteReader = mock(ByteReader.class);
        when(ByteFineIoReader.build(Matchers.<URI>any())).thenReturn(byteReader);

        mockStatic(IntFineIoReader.class);
        IntReader intReader = mock(IntReader.class);
        when(IntFineIoReader.build(Matchers.<URI>any())).thenReturn(intReader);

        mockStatic(LongFineIoReader.class);
        LongReader longReader = mock(LongReader.class);
        when(LongFineIoReader.build(Matchers.<URI>any())).thenReturn(longReader);

        when(byteReader.isReadable()).thenReturn(true);
        when(intReader.isReadable()).thenReturn(true);
        when(longReader.isReadable()).thenReturn(true);

        when(byteReader.get(1)).thenReturn((byte) 1);
        when(intReader.get(0)).thenReturn(1);
        when(longReader.get(0)).thenReturn(1L);

        doNothing().when(byteReader).release();
        doNothing().when(intReader).release();
        doNothing().when(longReader).release();
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
    }
}