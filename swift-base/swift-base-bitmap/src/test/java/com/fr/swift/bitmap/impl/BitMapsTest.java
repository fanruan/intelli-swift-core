package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.util.Crasher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by Lyon on 2018/6/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RoaringMutableBitMap.class, AllShowBitMap.class, RangeBitmap.class, IdBitMap.class, ByteBuffer.class,
        BitMaps.class, Crasher.class})
public class BitMapsTest {

    @Test
    public void test2Array() {
        IntList list = IntListFactory.createIntList(3);
        list.add(2);
        list.add(4);
        list.add(6);
        ImmutableBitMap bitMap = BitMaps.newImmutableBitMap(list);
        IntArray array = BitMaps.traversal2Array(bitMap);
        Assert.assertEquals(array.get(0), 2);
        Assert.assertEquals(array.get(1), 4);
        Assert.assertEquals(array.get(2), 6);
    }

    @Test
    public void testOf() {
        mockStatic(RoaringMutableBitMap.class, AllShowBitMap.class, RangeBitmap.class, IdBitMap.class, ByteBuffer.class, Crasher.class);

        byte[] bytes = new byte[0];
        ByteBuffer buf = mock(ByteBuffer.class);
        when(ByteBuffer.wrap(bytes, 0, bytes.length)).thenReturn(buf);

        when(buf.get()).thenReturn(
                BitMapType.ROARING_IMMUTABLE.getHead(), BitMapType.ROARING_MUTABLE.getHead(),
                BitMapType.ALL_SHOW.getHead(),
                BitMapType.RANGE.getHead(),
                BitMapType.ID.getHead(),
                BitMapType.BIT_SET_IMMUTABLE.getHead());
        // roaring
        BitMaps.of(bytes, 0, bytes.length);
        BitMaps.of(bytes, 0, bytes.length);

        verifyStatic(RoaringMutableBitMap.class, times(2));
        RoaringMutableBitMap.ofBuffer(buf);
        // all show
        BitMaps.of(bytes, 0, bytes.length);

        verify(buf).order(ByteOrder.LITTLE_ENDIAN);
        verify(buf).getInt();
        verifyStatic(AllShowBitMap.class);
        AllShowBitMap.of(anyInt());
        // range
        BitMaps.of(bytes, 0, bytes.length);

        verify(buf, times(2)).order(ByteOrder.LITTLE_ENDIAN);
        verify(buf, times(3)).getInt();
        verifyStatic(RangeBitmap.class);
        RangeBitmap.of(anyInt(), anyInt());
        // id
        BitMaps.of(bytes, 0, bytes.length);

        verify(buf, times(3)).order(ByteOrder.LITTLE_ENDIAN);
        verify(buf, times(4)).getInt();
        verifyStatic(IdBitMap.class);
        IdBitMap.of(anyInt());
        // default
        BitMaps.of(bytes, 0, bytes.length);

        verifyStatic(Crasher.class);
        Crasher.crash(anyString());
    }
}
