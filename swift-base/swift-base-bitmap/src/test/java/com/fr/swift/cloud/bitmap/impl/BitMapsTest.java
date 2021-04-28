package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.BitMapType;
import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.MutableBitMap;
import com.fr.swift.cloud.structure.array.IntArray;
import com.fr.swift.cloud.structure.array.IntList;
import com.fr.swift.cloud.structure.array.IntListFactory;
import com.fr.swift.cloud.util.Assert;
import com.fr.swift.cloud.util.Crasher;
import com.fr.swift.cloud.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by Lyon on 2018/6/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RoaringMutableBitMap.class, AllShowBitMap.class, RangeBitmap.class, IdBitMap.class, ByteBuffer.class,
        BitMaps.class, Crasher.class, Assert.class, IoUtil.class})
public class BitMapsTest {

    @Test
    public void test2Array() {
        IntList list = IntListFactory.createIntList(3);
        list.add(2);
        list.add(4);
        list.add(6);
        ImmutableBitMap bitMap = BitMaps.newImmutableBitMap(list);
        IntArray array = BitMaps.traversal2Array(bitMap);
        assertEquals(array.get(0), 2);
        assertEquals(array.get(1), 4);
        assertEquals(array.get(2), 6);
    }

    @Test
    public void ofStream() throws Exception {
        mockStatic(Assert.class);

        InputStream input = mock(InputStream.class);

        when(input.read()).thenReturn(
                (int) BitMapType.ROARING_IMMUTABLE.getHead(), (int) BitMapType.ROARING_MUTABLE.getHead(),
                (int) BitMapType.EMPTY.getHead(),
                (int) BitMapType.ALL_SHOW.getHead())
                .thenThrow(IOException.class);

        MutableBitMap bitmap = mock(MutableBitMap.class);

        mockStatic(IoUtil.class);
        // roaring
        mockStatic(RoaringMutableBitMap.class);
        when(RoaringMutableBitMap.ofStream(input)).thenReturn(bitmap);

        assertEquals(bitmap, BitMaps.ofStream(input));
        assertEquals(bitmap, BitMaps.ofStream(input));
        // empty
        assertEquals(BitMaps.EMPTY_IMMUTABLE, BitMaps.ofStream(input));
        // default
        ReadableByteChannel channel = mock(ReadableByteChannel.class);
        mockStatic(Channels.class);
        when(Channels.newChannel(input)).thenReturn(channel);

        spy(BitMaps.class);
        doReturn(bitmap)
                .when(BitMaps.class, "ofChannel", eq(channel), any(BitMapType.class));

        assertEquals(bitmap, BitMaps.ofStream(input));
        // io exception
        assertEquals(BitMaps.EMPTY_IMMUTABLE, BitMaps.ofStream(input));

        verifyStatic(Assert.class, times(5));
        Assert.notNull(input);

        verifyStatic(IoUtil.class, times(5));
        IoUtil.close(input);
    }

    @Test
    public void ofChannel() throws Exception {
        ReadableByteChannel channel = mock(ReadableByteChannel.class);

        ByteBuffer buf = mock(ByteBuffer.class);
        when(buf.order(ByteOrder.LITTLE_ENDIAN)).thenReturn(buf);
        mockStatic(ByteBuffer.class);
        when(ByteBuffer.allocate(8)).thenReturn(buf);

        when(channel.read(buf)).thenReturn(4, 8, 4, 0).thenThrow(new IOException());

        mockStatic(IoUtil.class);

        when(buf.getInt()).thenReturn(1);

        mockStatic(AllShowBitMap.class, RangeBitmap.class, IdBitMap.class, Crasher.class);
        // all show
        ImmutableBitMap bitmap = mock(ImmutableBitMap.class);
        when(AllShowBitMap.of(1)).thenReturn(bitmap);

        assertEquals(bitmap,
                Whitebox.invokeMethod(BitMaps.class, "ofChannel", channel, BitMapType.ALL_SHOW));
        // range
        when(RangeBitmap.of(1, 1)).thenReturn(bitmap);

        assertEquals(bitmap,
                Whitebox.invokeMethod(BitMaps.class, "ofChannel", channel, BitMapType.RANGE));
        // id
        when(IdBitMap.of(1)).thenReturn(bitmap);

        assertEquals(bitmap,
                Whitebox.invokeMethod(BitMaps.class, "ofChannel", channel, BitMapType.ID));
        // default
        Whitebox.invokeMethod(BitMaps.class, "ofChannel", channel, BitMapType.BIT_SET_IMMUTABLE);

        try {
            // io exception
            Whitebox.invokeMethod(BitMaps.class, "ofChannel", channel, BitMapType.BIT_SET_IMMUTABLE);
            fail();
        } catch (Exception ignore) {
        }

        verify(channel, times(5)).read(buf);
        verify(buf, times(4)).flip();

        verifyStatic(IoUtil.class, times(5));
        IoUtil.close(channel);

        verifyStatic(Crasher.class);
        Crasher.crash(anyString());
    }
}
