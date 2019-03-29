package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
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
    public void ofBuffer() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(3);
        buf.position(1);

        MutableRoaringBitmap mutableRoaringBitmap = mock(MutableRoaringBitmap.class, Mockito.RETURNS_DEEP_STUBS);
        whenNew(MutableRoaringBitmap.class).withNoArguments().thenReturn(mutableRoaringBitmap);

        ByteArrayInputStream byteArrayInputStream = mock(ByteArrayInputStream.class);
        whenNew(ByteArrayInputStream.class).withArguments(buf.array(), buf.position(), buf.limit() - buf.position())
                .thenReturn(byteArrayInputStream);

        DataInputStream dataInputStream = mock(DataInputStream.class);
        whenNew(DataInputStream.class).withArguments(byteArrayInputStream).thenReturn(dataInputStream);

        MutableBitMap mutableBitMap = mock(MutableBitMap.class);
        spy(RoaringMutableBitMap.class);
        doReturn(mutableBitMap).when(RoaringMutableBitMap.class);
        RoaringMutableBitMap.of(mutableRoaringBitmap);

        mockStatic(IoUtil.class);

        assertEquals(mutableBitMap, RoaringMutableBitMap.ofBuffer(buf));

        verify(mutableRoaringBitmap).deserialize(dataInputStream);

        doThrow(new IOException()).when(mutableRoaringBitmap).deserialize(dataInputStream);

        doReturn(mutableBitMap).when(RoaringMutableBitMap.class);
        RoaringMutableBitMap.of();

        assertEquals(mutableBitMap, RoaringMutableBitMap.ofBuffer(buf));

        verifyStatic(IoUtil.class, times(2));
        IoUtil.close(dataInputStream);
    }
}