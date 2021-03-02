package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.MutableBitMap;
import com.fr.swift.cloud.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.cloud.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/3/12
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RoaringMutableBitMap.class, IoUtil.class})
public class RoaringMutableBitMapTest {

    @Test
    public void ofStream() throws Exception {
        MutableRoaringBitmap mutableRoaringBitmap = mock(MutableRoaringBitmap.class, RETURNS_DEEP_STUBS);
        whenNew(MutableRoaringBitmap.class).withNoArguments().thenReturn(mutableRoaringBitmap);

        DataInputStream dataInputStream = mock(DataInputStream.class);
        InputStream input = mock(InputStream.class);
        whenNew(DataInputStream.class).withArguments(input).thenReturn(dataInputStream);

        MutableBitMap mutableBitMap = mock(MutableBitMap.class);
        PowerMockito.spy(RoaringMutableBitMap.class);
        doReturn(mutableBitMap).when(RoaringMutableBitMap.class);
        RoaringMutableBitMap.of(mutableRoaringBitmap);

        mockStatic(IoUtil.class);

        assertEquals(mutableBitMap, RoaringMutableBitMap.ofStream(input));

        verify(mutableRoaringBitmap).deserialize(dataInputStream);

        doThrow(new IOException()).when(mutableRoaringBitmap).deserialize(dataInputStream);

        doReturn(mutableBitMap).when(RoaringMutableBitMap.class);
        RoaringMutableBitMap.of();

        assertEquals(mutableBitMap, RoaringMutableBitMap.ofStream(input));

        verifyStatic(IoUtil.class, times(2));
        IoUtil.close(dataInputStream);
    }
}