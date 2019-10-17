package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author anchore
 * @date 2019/3/19
 */
public class BitmapMemMeterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void meter() {
        ImmutableBitMap bitmap = mock(BaseRoaringBitMap.class);
        when(bitmap.getType()).thenReturn(
                BitMapType.ROARING_MUTABLE, BitMapType.ROARING_IMMUTABLE,
                BitMapType.RANGE, BitMapType.ALL_SHOW, BitMapType.ID,
                BitMapType.BIT_SET_IMMUTABLE, BitMapType.BIT_SET_MUTABLE);

        assertEquals(0, BitmapMemMeter.meter(null));

        MutableRoaringBitmap roaringBitmap = mock(MutableRoaringBitmap.class);
        when(roaringBitmap.getLongSizeInBytes()).thenReturn(1L);
        Whitebox.setInternalState(bitmap, "bitmap", roaringBitmap);
        assertEquals(1, BitmapMemMeter.meter(bitmap));
        assertEquals(1, BitmapMemMeter.meter(bitmap));

        assertEquals(8, BitmapMemMeter.meter(bitmap));
        assertEquals(8, BitmapMemMeter.meter(bitmap));
        assertEquals(8, BitmapMemMeter.meter(bitmap));

        try {
            BitmapMemMeter.meter(bitmap);
            fail();
        } catch (UnsupportedOperationException ignore) {
        }
        try {
            BitmapMemMeter.meter(bitmap);
            fail();
        } catch (UnsupportedOperationException ignore) {
        }
    }
}