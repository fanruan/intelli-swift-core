package com.fr.swift.cube.io.impl;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.input.ByteArrayReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/4/4
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({BitMaps.class})
public class BaseBitmapReaderTest {
    @Mock
    private ByteArrayReader byteArrayReader;

    @Test
    public void get() {
        ImmutableBitMap bitmap = mock(ImmutableBitMap.class);

        when(byteArrayReader.getStream(1)).thenReturn(mock(InputStream.class));

        mockStatic(BitMaps.class);
        when(BitMaps.ofStream(byteArrayReader.getStream(1))).thenReturn(bitmap);

        assertEquals(bitmap, new BaseBitmapReader(byteArrayReader).get(1));
    }

    @Test
    public void isReadable() {
        when(byteArrayReader.isReadable()).thenReturn(true);

        assertFalse(new BaseBitmapReader(null).isReadable());
        assertTrue(new BaseBitmapReader(byteArrayReader).isReadable());

        verify(byteArrayReader).isReadable();
    }

    @Test
    public void release() {
        new BaseBitmapReader(byteArrayReader).release();

        verify(byteArrayReader).release();
    }
}