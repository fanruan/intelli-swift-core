package com.fr.swift.segment.operator.collate.segment.bitmap;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by lyon on 2019/2/21.
 */
public class BitMapShifterTest {

    @Test
    public void shift() {
        ImmutableBitMap bitMap = BitMaps.newAllShowBitMap(10);
        BitMapShifter shifter = BitMapShifterFactory.create(0, 10, bitMap);
        check(BitMaps.newRangeBitmap(0, 10), shifter.shift(bitMap));

        bitMap = BitMaps.newAllShowBitMap(10);
        shifter = BitMapShifterFactory.create(2, 10, bitMap);
        check(BitMaps.newRangeBitmap(2, 12), shifter.shift(bitMap));

        bitMap = of(1, 3, 5);
        shifter = BitMapShifterFactory.create(0, 6, of(0, 1, 2, 3, 4, 5));
        check(of(1, 3, 5), shifter.shift(bitMap));

        // delete {0, 2, 4}
        shifter = BitMapShifterFactory.create(0, 6, of(1, 3, 5));
        check(of(0, 1, 2), shifter.shift(bitMap));

        // delete { 4 }
        shifter = BitMapShifterFactory.create(0, 6, of(0, 1, 2, 3, 5));
        check(of(1, 3, 4), shifter.shift(bitMap));

        // delete { 4 } and start = 3
        shifter = BitMapShifterFactory.create(3, 6, of(0, 1, 2, 3, 5));
        check(of(4, 6, 7), shifter.shift(bitMap));

        // short[]
        bitMap = of(1, (int) Short.MAX_VALUE - 1);
        shifter = BitMapShifterFactory.create(0, Short.MAX_VALUE, of(0, 1, (int) Short.MAX_VALUE - 1));
        check(of(1, 2), shifter.shift(bitMap));

        // int[]
        bitMap = of(1, (int) Short.MAX_VALUE);
        shifter = BitMapShifterFactory.create(0, Short.MAX_VALUE * 2, of(0, 1, (int) Short.MAX_VALUE));
        check(of(1, 2), shifter.shift(bitMap));
    }

    private ImmutableBitMap of(Integer... bits) {
        MutableBitMap mutableBitMap = BitMaps.newRoaringMutable();
        for (Integer bit : bits) {
            mutableBitMap.add(bit);
        }
        return mutableBitMap;
    }

    private void check(ImmutableBitMap expected, ImmutableBitMap actual) {
        assertEquals(expected.getCardinality(), actual.getCardinality());
        assertTrue(expected.getAndNot(actual).isEmpty());
    }
}